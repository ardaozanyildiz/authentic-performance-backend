package com.couture.couturebackend.services;

import com.couture.couturebackend.models.Appointment;
import com.couture.couturebackend.models.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // --- 1. COURRIEL DE LA PAGE CONTACT (VERS LE GÉRANT) ---
    @Async
    public void sendContactEmailToManager(EmailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // DESTINATAIRE MODIFIÉ POUR TON TEST :
            helper.setTo("ardasshopify@gmail.com");
            helper.setFrom("aysunonder8080@gmail.com");
            helper.setReplyTo(request.getEmail());
            helper.setSubject("Nouveau message : " + request.getSubject());

            String htmlContent = "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;'>"
                    + "<div style='background-color: #C9B59C; padding: 20px; text-align: center;'>"
                    + "<h2 style='color: white; margin: 0; letter-spacing: 1px;'>Nouveau Message du Site Web</h2>"
                    + "</div>"
                    + "<div style='padding: 30px; background-color: #F9F8F6;'>"
                    + "<p style='font-size: 16px; margin-bottom: 10px;'><strong>Nom :</strong> " + request.getName() + "</p>"
                    + "<p style='font-size: 16px; margin-bottom: 20px;'><strong>Courriel :</strong> <a href='mailto:" + request.getEmail() + "' style='color: #C9B59C;'>" + request.getEmail() + "</a></p>"
                    + "<div style='background-color: white; padding: 20px; border-left: 4px solid #C9B59C; border-radius: 4px;'>"
                    + "<h3 style='margin-top: 0; color: #333; font-size: 14px; text-transform: uppercase;'>Message :</h3>"
                    + "<p style='white-space: pre-wrap; line-height: 1.6; margin: 0;'>" + request.getMessage() + "</p>"
                    + "</div>"
                    + "</div>"
                    + "<div style='background-color: #eee; padding: 15px; text-align: center; font-size: 12px; color: #888;'>"
                    + "Ce courriel a été envoyé automatiquement depuis le formulaire de contact du site web."
                    + "</div>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erreur (Contact) : " + e.getMessage());
        }
    }

    // --- 2. NOTIFICATION DE RENDEZ-VOUS (VERS LE GÉRANT) ---
    @Async
    public void sendNotificationToManager(Appointment app) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // DESTINATAIRE MODIFIÉ POUR TON TEST :
            helper.setTo("ardasshopify@gmail.com");
            helper.setFrom("aysunonder8080@gmail.com");
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
            System.err.println("Erreur notification gérant : " + e.getMessage());
        }
    }

    // --- 3. CONFIRMATION DE RENDEZ-VOUS (VERS LE CLIENT) AVEC BOUTON D'ANNULATION ---
    @Async
    public void sendConfirmationToClient(Appointment app) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Le destinataire est le client (tu vas entrer ton courriel dans le formulaire du site)
            helper.setTo(app.getClientEmail());
            helper.setFrom("aysunonder8080@gmail.com");
            helper.setSubject("Confirmation de votre rendez-vous - Authentic Performance");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm");
            String dateLabel = app.getAppointmentDate().format(formatter);

            // Le lien dynamique généré avec l'ID du rendez-vous
            String lienAnnulation = "http://localhost:5173/annuler-rdv/" + app.getId();

            String htmlContent = "<div style='font-family: Arial, sans-serif; border: 1px solid #C9B59C; padding: 30px; border-radius: 10px; max-width: 600px; margin: 0 auto;'>"
                    + "<h2 style='color: #C9B59C; text-align: center;'>Votre rendez-vous est confirmé !</h2>"
                    + "<p>Bonjour <strong>" + app.getClientName() + "</strong>,</p>"
                    + "<p>Nous avons bien reçu votre demande. Voici les détails de votre rencontre avec nos experts pour le service de <strong>" + app.getServiceType() + "</strong> :</p>"
                    + "<div style='background-color: #F9F8F6; padding: 20px; border-radius: 8px; margin: 25px 0;'>"
                    + "<p style='margin: 5px 0; font-size: 16px;'> <strong>Date et heure :</strong> " + dateLabel + "</p>"
                    + "<p style='margin: 15px 0 5px 0; font-size: 16px;'> <strong>Lieu de l'atelier :</strong><br>9600 Rue Meilleur, Suite #820-4<br>Montréal, QC H2N 2E3</p>"
                    + "</div>"
                    + "<div style='text-align: center; margin: 30px 0;'>"
                    + "<p style='color: #666; font-size: 14px; margin-bottom: 10px;'>Vous avez un empêchement ? Vous pouvez libérer votre place en cliquant ci-dessous :</p>"
                    + "<a href='" + lienAnnulation + "' style='background-color: #4a3728; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;'>Annuler mon rendez-vous</a>"
                    + "</div>"
                    + "<hr style='border: 0; border-top: 1px solid #eee; margin: 30px 0;'>"
                    + "<p style='font-size: 14px; color: #555;'><strong>L'équipe Authentic Performance Production</strong><br>T. +1 (514) 337-1951</p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erreur confirmation client : " + e.getMessage());
        }
    }

    // --- 4. NOUVEAU : AVERTIR LE GÉRANT DE L'ANNULATION ---
    @Async
    public void sendCancellationToManager(Appointment app) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // DESTINATAIRE MODIFIÉ POUR TON TEST :
            helper.setTo("ardasshopify@gmail.com");
            helper.setFrom("aysunonder8080@gmail.com");
            helper.setSubject(" ANNULATION : " + app.getClientName());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm");
            String dateLabel = app.getAppointmentDate().format(formatter);

            String htmlContent = "<div style='font-family: Arial, sans-serif; border: 1px solid #cc0000; padding: 20px; border-radius: 10px;'>"
                    + "<h2 style='color: #cc0000;'>Un rendez-vous a été annulé par le client</h2>"
                    + "<p><strong>Client :</strong> " + app.getClientName() + "</p>"
                    + "<p><strong>Date libérée :</strong> " + dateLabel + "</p>"
                    + "<p><strong>Service qui était prévu :</strong> " + app.getServiceType() + "</p>"
                    + "<hr style='border: 0; border-top: 1px solid #eee;'>"
                    + "<p style='font-size: 14px; color: #333;'>La plage horaire est automatiquement redevenue disponible sur le site Web.</p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erreur courriel d'annulation gérant : " + e.getMessage());
        }
    }
}