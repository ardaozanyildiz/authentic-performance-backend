package com.couture.couturebackend.repositories;

import com.couture.couturebackend.models.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List; // N'oublie pas cet import !
@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    // Vérifie si l'heure exacte est déjà prise dans MongoDB
    boolean existsByAppointmentDate(LocalDateTime appointmentDate);

    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
}