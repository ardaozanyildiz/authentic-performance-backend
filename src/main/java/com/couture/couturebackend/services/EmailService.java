package com.couture.couturebackend.services;

import com.couture.couturebackend.models.Appointment;
import com.couture.couturebackend.models.EmailRequest;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    @Async
    public void sendContactEmailToManager(EmailRequest request) {
        try {
            Resend resend = new Resend(resendApiKey);

            String htmlContent = "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;'>"
                    + "<div style='background-color: #C9B59C; padding: 20px; text-align: center;'>"
                    + "<h2 style='color: white; margin: 0; letter-spacing: 1px;'>Nouveau Message du Site Web</h2>"
                    + "</div>"
                    + "<div style='padding: 30px; background-color: #F9F8F6;'>"
                    + "<p style='font-size: 16px; margin-bottom: 10px;'><strong>Nom :</strong> " + request.getName() + "</p>"
                    + "<p style='font-size: 16px; margin-bottom: 20px;'><strong>Courriel :</strong> " + request.getEmail() + "</p>"
                    + "<div style='background-color: white; padding: 20px; border-left: 4px solid #C9B59C; border-radius: 4px;'>"
                    + "<h3 style='margin-top: 0; color: #333; font-size: 14px; text-transform: uppercase;'>Message :</h3>"
                    + "<p style='white-space: pre-wrap; line-height: 1.6; margin: 0;'>" + request.getMessage() + "</p>"
                    + "</div>"
                    + "</div>"
                    + "<div style='background-color: #eee; padding: 15px; text-align: center; font-size: 12px; color: #888;'>"
                    + "Ce courriel a été envoyé automatiquement depuis le formulaire de contact du site web."
                    + "</div>"
                    + "</div>";

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Authentic Performance <rendezvous@authenticperformanceproduction.ca>")
                    .to("aysunonder8080@gmail.com")
                    .replyTo(request.getEmail())
                    .subject("Nouveau message : " + request.getSubject())
                    .html(htmlContent)
                    .build();

            resend.emails().send(params);
        } catch (ResendException e) {
            System.err.println("Erreur (Contact) : " + e.getMessage());
        }
    }

    @Async
    public void sendNotificationToManager(Appointment app) {
        try {
            Resend resend = new Resend(resendApiKey);

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

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Authentic Performance <rendezvous@authenticperformanceproduction.ca>")
                    .to("aysunonder8080@gmail.com")
                    .subject("NOUVEAU RENDEZ-VOUS : " + app.getClientName())
                    .html(htmlContent)
                    .build();

            resend.emails().send(params);
        } catch (ResendException e) {
            System.err.println("Erreur notification gérant : " + e.getMessage());
        }
    }

    @Async
    public void sendConfirmationToClient(Appointment app) {
        try {
            Resend resend = new Resend(resendApiKey);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm");
            String dateLabel = app.getAppointmentDate().format(formatter);

            String lienAnnulation = "https://authenticperformanceproduction.ca/annuler-rdv/" + app.getId();

            String htmlContent = "<div style='font-family: Arial, sans-serif; border: 1px solid #C9B59C; padding: 30px; border-radius: 10px; max-width: 600px; margin: 0 auto;'>"
                    + "<h2 style='color: #C9B59C; text-align: center;'>Votre rendez-vous est confirmé !</h2>"
                    + "<p>Bonjour <strong>" + app.getClientName() + "</strong>,</p>"
                    + "<p>Nous avons bien reçu votre demande. Voici les détails de votre rencontre avec nos experts pour le service de <strong>" + app.getServiceType() + "</strong> :</p>"
                    + "<div style='background-color: #F9F8F6; padding: 20px; border-radius: 8px; margin: 25px 0;'>"
                    + "<p style='margin: 5px 0; font-size: 16px;'><strong>Date et heure :</strong> " + dateLabel + "</p>"
                    + "<p style='margin: 15px 0 5px 0; font-size: 16px;'><strong>Lieu de l'atelier :</strong><br>9600 Rue Meilleur, Suite #820-4<br>Montréal, QC H2N 2E3</p>"
                    + "</div>"
                    + "<div style='text-align: center; margin: 30px 0;'>"
                    + "<a href='" + lienAnnulation + "' style='background-color: #4a3728; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;'>Annuler mon rendez-vous</a>"
                    + "</div>"
                    + "<hr style='border: 0; border-top: 1px solid #eee; margin: 30px 0;'>"
                    + "<p style='font-size: 14px; color: #555;'><strong>L'équipe Authentic Performance Production</strong><br>T. +1 (514) 337-1951</p>"
                    + "</div>";

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Authentic Performance <rendezvous@authenticperformanceproduction.ca>")
                    .to(app.getClientEmail())
                    .subject("Confirmation de votre rendez-vous - Authentic Performance")
                    .html(htmlContent)
                    .build();

            resend.emails().send(params);
        } catch (ResendException e) {
            System.err.println("Erreur confirmation client : " + e.getMessage());
        }
    }

    @Async
    public void sendCancellationToManager(Appointment app) {
        try {
            Resend resend = new Resend(resendApiKey);

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

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Authentic Performance <rendezvous@authenticperformanceproduction.ca>")
                    .to("aysunonder8080@gmail.com")
                    .subject("ANNULATION : " + app.getClientName())
                    .html(htmlContent)
                    .build();

            resend.emails().send(params);
        } catch (ResendException e) {
            System.err.println("Erreur annulation gérant : " + e.getMessage());
        }
    }
}