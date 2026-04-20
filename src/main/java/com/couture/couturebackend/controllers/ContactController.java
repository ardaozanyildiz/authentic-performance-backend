package com.couture.couturebackend.controllers;

import com.couture.couturebackend.models.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailRequest request) {
        try {
            // On utilise MimeMessage pour pouvoir envoyer du HTML
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Les infos du destinataire du gerant
            helper.setTo("aysunonder8080@gmail.com");
            helper.setFrom("aysunonder8080@gmail.com", "Site Web : " + request.getName());
            helper.setReplyTo(request.getEmail());
            helper.setSubject("Nouveau message : " + request.getSubject());

            // Le contenu (Formaté en HTML avec les couleurs de l'entreprise)
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

            // Le "true" ici indique que le contenu est du HTML et non du texte brut
            helper.setText(htmlContent, true);

            mailSender.send(message);
            return "Message envoyé avec succès !";

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'envoi.";
        }
    }
}