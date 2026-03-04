# Full-Stack E-Commerce Bookstore

---

## Key Features

* **Secure Authentication:** Custom login and registration flow powered by **Spring Security** and **BCrypt** password hashing.
* **Email Verification:** Account activation system using **Jakarta Mail** and unique UUID tokens.
* **Cart Management:** State-persistent shopping cart with real-time subtotal and total calculations.
* **Order History:** Complete tracking of past purchases for authenticated users.
* **Security First:** Integrated **CSRF protection**, role-based access control (RBAC), and protected API endpoints.
* **Responsive UI:** Clean, modern interface built with **Thymeleaf** and custom CSS.

---

## Technology Stack

| Layer             | Technologies                                               |
|:------------------|:-----------------------------------------------------------|
| **Backend**       | Java 21, Spring Boot 3.4, Spring Data JPA, Spring Security |
| **Frontend**      | Thymeleaf, HTML5, CSS3                                     |
| **Database**      | PostgreSQL                                                 |
| **DevOps/Tools**  | Docker, Maven, Lombok, MapStruct                           |
| **Communication** | Jakarta Mail (SMTP)                                        |
