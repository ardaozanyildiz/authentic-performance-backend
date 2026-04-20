package com.couture.couturebackend.controllers;

import com.couture.couturebackend.models.EmailRequest;
import com.couture.couturebackend.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ContactController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest request) {
        try {
            // Le courriel part en arrière-plan !
            emailService.sendContactEmailToManager(request);

            // React reçoit sa réponse tout de suite
            return ResponseEntity.ok().body("Message envoyé avec succès !");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur lors de l'envoi.");
        }
    }
}