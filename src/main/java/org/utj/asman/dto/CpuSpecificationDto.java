package org.utj.asman.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CpuSpecificationDto {

    private Long id;

    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;

    @NotBlank(message = "Model Name is required")
    private String model;

    @NotBlank(message = "Processor spec is required")
    private String processor;

    @NotBlank(message = "Memory spec is required")
    private String memory;

    @NotBlank(message = "Hard Disk spec is required")
    private String hardDisk;

    private LocalDate purchaseDate;

    private String supplier;
}