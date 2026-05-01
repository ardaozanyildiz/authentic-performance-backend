package com.couture.couturebackend.repositories;

import com.couture.couturebackend.models.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    boolean existsByAppointmentDate(LocalDateTime appointmentDate);

    boolean existsByClientEmailAndAppointmentDateAfter(String clientEmail, LocalDateTime date);

    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
}