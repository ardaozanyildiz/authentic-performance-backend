package com.couture.couturebackend.controllers;

import com.couture.couturebackend.models.Appointment;
import com.couture.couturebackend.repositories.AppointmentRepository;
import com.couture.couturebackend.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmailService emailService;

    // --- ROUTE 1 : PRENDRE UN RENDEZ-VOUS ---
    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody Appointment appointment) {
        LocalDateTime date = appointment.getAppointmentDate();

        // Règle : Seulement le vendredi
        if (date.getDayOfWeek() != DayOfWeek.FRIDAY) {
            return ResponseEntity.badRequest().body("Erreur : Les rendez-vous sont disponibles uniquement le vendredi.");
        }

        // Règle : Entre 12h et 17h
        int hour = date.getHour();
        if (hour < 12 || hour >= 17) {
            return ResponseEntity.badRequest().body("Erreur : Les rendez-vous doivent être entre 12h00 et 17h00.");
        }

        // Règle : Créneau déjà pris
        if (appointmentRepository.existsByAppointmentDate(date)) {
            return ResponseEntity.badRequest().body("Erreur : Ce créneau est déjà réservé par un autre client.");
        }

        // RÈGLE SÉCURITÉ PRO : Bloquer si le client a DÉJÀ un rendez-vous dans le futur
        if (appointmentRepository.existsByClientEmailAndAppointmentDateAfter(appointment.getClientEmail(), LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Erreur : Vous avez déjà un rendez-vous à venir avec cette adresse courriel.");
        }

        // Tout est beau, on sauvegarde !
        appointment.setStatus("CONFIRMED");
        Appointment saved = appointmentRepository.save(appointment);

        // On envoie les courriels en arrière-plan
        emailService.sendNotificationToManager(saved);
        emailService.sendConfirmationToClient(saved);

        return ResponseEntity.ok(saved);
    }

    // --- ROUTE 2 : AFFICHER LES HEURES DÉJÀ PRISES (Pour griser les boutons) ---
    @GetMapping("/booked-times")
    public ResponseEntity<List<String>> getBookedTimes(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(23, 59, 59);

        List<Appointment> dailyAppointments = appointmentRepository.findByAppointmentDateBetween(startOfDay, endOfDay);

        List<String> bookedTimes = new ArrayList<>();
        for (Appointment app : dailyAppointments) {
            String time = String.format("%02d:%02d", app.getAppointmentDate().getHour(), app.getAppointmentDate().getMinute());
            bookedTimes.add(time);
        }

        return ResponseEntity.ok(bookedTimes);
    }

    // --- ROUTE 3 : ANNULATION DU RENDEZ-VOUS ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelAppointment(@PathVariable String id) {
        try {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);

            if (appointmentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("{\"message\": \"Rendez-vous introuvable.\"}");
            }

            Appointment appointmentToCancel = appointmentOpt.get();

            // Supprimer le rendez-vous libère la disponibilité instantanément pour les autres
            appointmentRepository.deleteById(id);

            // Avertir le gérant de l'annulation
            emailService.sendCancellationToManager(appointmentToCancel);

            return ResponseEntity.ok("{\"message\": \"Rendez-vous annulé avec succès.\"}");

        } catch (Exception e) {
            System.err.println("Erreur lors de l'annulation : " + e.getMessage());
            return ResponseEntity.internalServerError().body("{\"message\": \"Erreur serveur lors de l'annulation.\"}");
        }
    }
}