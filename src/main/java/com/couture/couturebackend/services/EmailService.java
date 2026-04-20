package com.couture.couturebackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ... (Tu as peut-être déjà une méthode ici pour la page Contact) ...

    // --- NOUVELLE MÉTHODE POUR LE RENDEZ-VOUS ---
    public void sendAppointmentConfirmation(String toEmail, String clientName, String date, String time, String serviceType) {
        SimpleMailMessage message = new SimpleMailMessage();

        // IMPORTANT : Utilise l'adresse configurée dans ton application.properties
        message.setFrom("aysunonder8080@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Confirmation de rendez-vous - Authentic Performance Production");

        String emailBody = "Bonjour " + clientName + ",\n\n" +
                "Nous vous confirmons la réception de votre demande de rendez-vous. " +
                "Notre équipe vous attend pour discuter de votre projet de " + serviceType + ".\n\n" +
                "Détails de votre rendez-vous :\n" +
                "- Date : " + date + "\n" +
                "- Heure : " + time + "\n" +
                "- Lieu : 9600 Rue Meilleur, Suite #820-4, Montréal, QC H2N 2E3\n\n" +
                "Si vous avez des questions ou devez modifier ce rendez-vous, n'hésitez pas à nous répondre directement à ce courriel.\n\n" +
                "Au plaisir de vous rencontrer,\n\n" +
                "L'équipe Authentic Performance Production\n" +
                "T. +1 (514) 337-1951";

        message.setText(emailBody);
        mailSender.send(message);
    }
}