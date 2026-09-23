package com.dcare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String vehicleNumber;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer yearOfManufacture;

    private LocalDate registrationDate;
    private String chassisNumber;
    private String engineNumber;
    private UUID customerId;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    public Vehicle() {}

    public Vehicle(UUID id, String vehicleNumber, String make, String model, Integer yearOfManufacture, LocalDate registrationDate, String chassisNumber, String engineNumber, UUID customerId, ZonedDateTime createdAt) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.make = make;
        this.model = model;
        this.yearOfManufacture = yearOfManufacture;
        this.registrationDate = registrationDate;
        this.chassisNumber = chassisNumber;
        this.engineNumber = engineNumber;
        this.customerId = customerId;
        this.createdAt = createdAt;
    }

    public static VehicleBuilder builder() { return new VehicleBuilder(); }

    public static class VehicleBuilder {
        private UUID id;
        private String vehicleNumber;
        private String make;
        private String model;
        private Integer yearOfManufacture;
        private LocalDate registrationDate;
        private String chassisNumber;
        private String engineNumber;
        private UUID customerId;
        private ZonedDateTime createdAt;

        public VehicleBuilder id(UUID id) { this.id = id; return this; }
        public VehicleBuilder vehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; return this; }
        public VehicleBuilder make(String make) { this.make = make; return this; }
        public VehicleBuilder model(String model) { this.model = model; return this; }
        public VehicleBuilder yearOfManufacture(Integer yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; return this; }
        public VehicleBuilder registrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; return this; }
        public VehicleBuilder chassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; return this; }
        public VehicleBuilder engineNumber(String engineNumber) { this.engineNumber = engineNumber; return this; }
        public VehicleBuilder customerId(UUID customerId) { this.customerId = customerId; return this; }
        public VehicleBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Vehicle build() {
            return new Vehicle(id, vehicleNumber, make, model, yearOfManufacture, registrationDate, chassisNumber, engineNumber, customerId, createdAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getYearOfManufacture() { return yearOfManufacture; }
    public void setYearOfManufacture(Integer yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    public String getChassisNumber() { return chassisNumber; }
    public void setChassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; }
    public String getEngineNumber() { return engineNumber; }
    public void setEngineNumber(String engineNumber) { this.engineNumber = engineNumber; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
