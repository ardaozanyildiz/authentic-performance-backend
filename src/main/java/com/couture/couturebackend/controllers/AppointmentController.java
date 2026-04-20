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

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmailService emailService; // On utilise le Service, pas le mailSender

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody Appointment appointment) {
        LocalDateTime date = appointment.getAppointmentDate();

        if (date.getDayOfWeek() != DayOfWeek.FRIDAY) {
            return ResponseEntity.badRequest().body("Erreur : Les rendez-vous sont disponibles uniquement le vendredi.");
        }

        int hour = date.getHour();
        if (hour < 12 || hour >= 17) {
            return ResponseEntity.badRequest().body("Erreur : Les rendez-vous doivent être entre 12h00 et 17h00.");
        }

        if (appointmentRepository.existsByAppointmentDate(date)) {
            return ResponseEntity.badRequest().body("Erreur : Ce créneau est déjà réservé par un autre client.");
        }

        appointment.setStatus("CONFIRMED");
        Appointment saved = appointmentRepository.save(appointment);

        // Ces envois partent en arrière-plan instantanément !
        emailService.sendNotificationToManager(saved);
        emailService.sendConfirmationToClient(saved);

        return ResponseEntity.ok(saved);
    }

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
}