package com.exemple.QA.controller;

import com.exemple.QA.model.Role;
import com.exemple.QA.model.User;
import com.exemple.QA.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @GetMapping("/with-roles")
    public ResponseEntity<List<Map<String, Object>>> getUsersWithRoles() {
        List<User> users = userRepository.findAll();
        
        List<Map<String, Object>> usersWithRoles = users.stream()
            .filter(user -> user.getRoles().stream().noneMatch(role -> "admin".equalsIgnoreCase(role.getName())))
            .map(user -> {
                Map<String, Object> userWithRoles = new HashMap<>();
                userWithRoles.put("id", user.getId());
                userWithRoles.put("email", user.getEmail());
                userWithRoles.put("password", user.getPassword());
                userWithRoles.put("firstName", user.getFirstName());
                userWithRoles.put("lastName", user.getLastName());
                userWithRoles.put("company", user.getCompany());
                userWithRoles.put("city", user.getCity());
                userWithRoles.put("phoneNumber", user.getPhoneNumber());
                userWithRoles.put("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
                return userWithRoles;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(usersWithRoles);
    }

    
    @PostMapping("/register")
    public ResponseEntity<?> addUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cet utilisateur existe déjà.");
        }

        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
    @GetMapping("/by-name/{name}")
    public ResponseEntity<List<User>> getUsersByName(@PathVariable String name) {
        List<User> users = userRepository.findAll();

        List<User> usersByName = users.stream()
                .filter(user -> user.getFirstName().equalsIgnoreCase(name) || user.getLastName().equalsIgnoreCase(name))
                .collect(Collectors.toList());

        if (usersByName.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usersByName);
    }
   
    
    @GetMapping("/get-role/{email}")
    public ResponseEntity<?> getUserRoleByEmail(@PathVariable String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Role> roles = user.getRoles();

            if (!roles.isEmpty()) {
                Role role = roles.iterator().next();
                Long roleId = role.getId();

                return ResponseEntity.ok().body("{\"roleId\": " + roleId + "}");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun rôle trouvé pour l'utilisateur avec l'e-mail " + email);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun utilisateur trouvé avec l'e-mail " + email);
        }
    }
   
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        List<User> matchingUsers = userRepository.findAllByEmail(user.getEmail());

        for (User storedUser : matchingUsers) {
            if (storedUser.getPassword().equals(user.getPassword())) {
                String token = generateAuthToken(storedUser.getEmail());
                return ResponseEntity.ok(token);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    
   
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun utilisateur trouvé avec l'ID " + id);
        }
    }
    @GetMapping("/get-info/{email}")
    public ResponseEntity<?> getUserInfoByEmail(@PathVariable String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("email", user.getEmail());
            userInfo.put("firstName", user.getFirstName());
            userInfo.put("lastName", user.getLastName());
            userInfo.put("company", user.getCompany());
            userInfo.put("city", user.getCity());
            userInfo.put("phoneNumber", user.getPhoneNumber());

            return ResponseEntity.ok().body(userInfo);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun utilisateur trouvé avec l'e-mail " + email);
        }}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        Optional<User> existingUserOptional = userRepository.findById(id);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(user.getPassword());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setCompany(user.getCompany());
            existingUser.setCity(user.getCity());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            existingUser.setRoles(user.getRoles()); // Mettre à jour les rôles si nécessaire
            
            User updatedUser = userRepository.save(existingUser);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun utilisateur trouvé avec l'ID " + id);
        }
    }

    private String generateAuthToken(String email) {
        return "Authentication token";
    }
}
