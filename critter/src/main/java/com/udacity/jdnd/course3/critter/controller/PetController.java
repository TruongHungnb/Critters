package com.udacity.jdnd.course3.critter.controller;

import com.udacity.jdnd.course3.critter.dto.PetDTO;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.exceptions.MissingDataException;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.UserService;
import com.udacity.jdnd.course3.critter.exceptions.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.exceptions.PetNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    private static final String []  PROPERTIES_TO_IGNORE_ON_COPY = { "id" };

    @Autowired
    PetService petService;

    @Autowired
    UserService userService;

    @PostMapping("/{ownerId}")
    public PetDTO updatePet(@PathVariable(name="ownerId") Long ownerId, @RequestBody PetDTO petDTO){
        petDTO.setOwnerId(ownerId);
        return savePet(petDTO);
    }

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) throws CustomerNotFoundException, MissingDataException {
        // ID c� null kh�ng
        long petId = Optional.ofNullable(petDTO.getId()).orElse(-1L);

        // l?y pet v? n?u n� t?n t?i
        Pet p = petService.findPet(Long.valueOf(petId)).orElseGet(Pet::new);

        // sao ch�p �?u v�o c?a ng�?i d�ng v�o pet hi?n c�
        BeanUtils.copyProperties(petDTO, p, PROPERTIES_TO_IGNORE_ON_COPY);

        // l�u pet c?a ch?
        p = petService.save(p, petDTO.getOwnerId());

        // tr? v? updated DTO
        PetDTO dto = new PetDTO();
        BeanUtils.copyProperties(p, dto);
        dto.setOwnerId(p.getOwner().getId());
        return dto;
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) throws PetNotFoundException {
        PetDTO dto = new PetDTO();
        Pet p = petService.findPet(petId).orElseThrow(() -> new PetNotFoundException("ID: " + petId));
        BeanUtils.copyProperties(p, dto);
        dto.setOwnerId(p.getOwner().getId());
        return dto;
    }

    @GetMapping
    public List<PetDTO> getPets(){
        List<Pet> pets = petService.findAllPets();
        return copyPetsToPetsDTO(pets);
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        List<Pet> pets = petService.findPetByOwner(Long.valueOf(ownerId));
        return copyPetsToPetsDTO(pets);
    }

    private List<PetDTO> copyPetsToPetsDTO(List<Pet> pets) {
        List<PetDTO> dtos = new ArrayList<>();
        pets.forEach(pet -> {
            dtos.add(new PetDTO(
                    pet.getId().longValue(),
                    pet.getType(),
                    pet.getName(),
                    pet.getOwner().getId().longValue(),
                    pet.getBirthDate(),
                    pet.getNotes()));
        });

        return dtos;
    }
}
