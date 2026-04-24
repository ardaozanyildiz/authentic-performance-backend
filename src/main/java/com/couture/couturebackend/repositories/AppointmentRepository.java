package com.couture.couturebackend.repositories;

import com.couture.couturebackend.models.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    // Vérifie si l'heure exacte est déjà prise par quelqu'un d'autre
    boolean existsByAppointmentDate(LocalDateTime appointmentDate);

    // NOUVELLE RÈGLE PRO : Vérifie si le client a déjà un rendez-vous dans le futur
    boolean existsByClientEmailAndAppointmentDateAfter(String clientEmail, LocalDateTime date);

    // Trouve tous les rendez-vous d'une journée spécifique (pour bloquer les heures dans le calendrier)
    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
}