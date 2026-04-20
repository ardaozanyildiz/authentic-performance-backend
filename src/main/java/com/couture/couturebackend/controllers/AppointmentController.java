package com.couture.couturebackend.controllers;

import com.couture.couturebackend.models.Appointment;
import com.couture.couturebackend.repositories.AppointmentRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody Appointment appointment) {
        LocalDateTime date = appointment.getAppointmentDate();

        // 1. SÉCURITÉ : Vérifier si c'est un vendredi
        if (date.getDayOfWeek() != DayOfWeek.FRIDAY) {
            return ResponseEntity.badRequest().body("Erreur : Les rendez-vous sont disponibles uniquement le vendredi.");
        }

        // 2. SÉCURITÉ : Vérifier si l'heure est entre 12h et 17h (16h30 est le dernier créneau)
        int hour = date.getHour();
        if (hour < 12 || hour >= 17) {
            return ResponseEntity.badRequest().body("Erreur : Les rendez-vous doivent être entre 12h00 et 17h00.");
        }

        // 3. SÉCURITÉ : Vérifier si la plage est déjà occupée dans la BD
        if (appointmentRepository.existsByAppointmentDate(date)) {
            return ResponseEntity.badRequest().body("Erreur : Ce créneau est déjà réservé par un autre client.");
        }

        // 4. ENREGISTREMENT
        appointment.setStatus("CONFIRMED");
        Appointment saved = appointmentRepository.save(appointment);

        // 5. NOTIFICATIONS AUTOMATIQUES (Gérant ET Client)
        sendNotificationToManager(saved);
        sendConfirmationToClient(saved); // <--- L'appel pour envoyer le courriel au client est ici !

        return ResponseEntity.ok(saved);
    }

    // --- ROUTE : Pour envoyer les heures indisponibles à React ---
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

    // ----------------------------------------------------------------------
    // MÉTHODES PRIVÉES POUR LES COURRIELS
    // ----------------------------------------------------------------------

    // Courriel pour le gérant (Ton code original)
    private void sendNotificationToManager(Appointment app) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo("aysunonder8080@gmail.com");
            helper.setSubject("NOUVEAU RENDEZ-VOUS : " + app.getClientName());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm");
            String dateLabel = app.getAppointmentDate().format(formatter);

            String htmlContent = "<div style='font-family: Arial, sans-serif; border: 1px solid #C9B59C; padding: 20px; border-radius: 10px;'>"
                    + "<h2 style='color: #C9B59C;'>Nouvelle réservation reçue !</h2>"
                    + "<p><strong>Client :</strong> " + app.getClientName() + "</p>"
                    + "<p><strong>Date :</strong> " + dateLabel + "</p>"
                    + "<p><strong>Service demandé :</strong> " + app.getServiceType() + "</p>"
                    + "<p><strong>Courriel client :</strong> " + app.getClientEmail() + "</p>"
                    + "<hr style='border: 0; border-top: 1px solid #eee;'>"
                    + "<p style='font-size: 12px; color: #888;'>Ceci est une notification automatique de votre site web Authentic Performance Production.</p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification au gérant : " + e.getMessage());
        }
    }

    // --- NOUVELLE MÉTHODE : Courriel pour le client ---
    private void sendConfirmationToClient(Appointment app) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // On envoie le courriel à l'adresse que le client a inscrite dans le formulaire
            helper.setTo(app.getClientEmail());
            helper.setFrom("aysunonder8080@gmail.com"); // Toujours bien de spécifier l'expéditeur
            helper.setSubject("Confirmation de votre rendez-vous - Authentic Performance");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm");
            String dateLabel = app.getAppointmentDate().format(formatter);

            // Design du courriel adapté pour le client
            String htmlContent = "<div style='font-family: Arial, sans-serif; border: 1px solid #C9B59C; padding: 30px; border-radius: 10px; max-width: 600px; margin: 0 auto;'>"
                    + "<h2 style='color: #C9B59C; text-align: center;'>Votre rendez-vous est confirmé !</h2>"
                    + "<p>Bonjour <strong>" + app.getClientName() + "</strong>,</p>"
                    + "<p>Nous avons bien reçu votre demande. Voici les détails de votre rencontre avec nos experts pour le service de <strong>" + app.getServiceType() + "</strong> :</p>"
                    + "<div style='background-color: #F9F8F6; padding: 20px; border-radius: 8px; margin: 25px 0;'>"
                    + "<p style='margin: 5px 0; font-size: 16px;'>📅 <strong>Date et heure :</strong> " + dateLabel + "</p>"
                    + "<p style='margin: 15px 0 5px 0; font-size: 16px;'>📍 <strong>Lieu de l'atelier :</strong><br>9600 Rue Meilleur, Suite #820-4<br>Montréal, QC H2N 2E3</p>"
                    + "</div>"
                    + "<p>Si vous avez des questions ou si vous devez annuler votre rendez-vous, n'hésitez pas à répondre directement à ce courriel.</p>"
                    + "<p>Au plaisir de vous rencontrer !</p>"
                    + "<hr style='border: 0; border-top: 1px solid #eee; margin: 30px 0;'>"
                    + "<p style='font-size: 14px; color: #555;'><strong>L'équipe Authentic Performance Production</strong><br>T. +1 (514) 337-1951</p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la confirmation au client : " + e.getMessage());
        }
    }
}