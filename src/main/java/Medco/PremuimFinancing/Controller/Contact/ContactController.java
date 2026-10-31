package Medco.PremuimFinancing.Controller.Contact;

import Medco.PremuimFinancing.Dto.Request.Contact.ContactRequest;
import Medco.PremuimFinancing.Dto.Response.MessageResponse;
import Medco.PremuimFinancing.Service.Messaging.EmailOutboxService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
@RequestMapping("/api/sebna/contact")
public class ContactController {

    private final EmailOutboxService emailOutboxService;

    @Value("${contact.recipient.email:sebnascinfo@gmail.com}")
    private String contactRecipient;

    public ContactController(EmailOutboxService emailOutboxService) {
        this.emailOutboxService = emailOutboxService;
    }

    // Public landing page contact form: forwards the message to the Sebna inbox
    @PostMapping
    public ResponseEntity<?> sendContactMessage(@Valid @RequestBody ContactRequest request) {
        String name = (request.getFirstName() + " " + (request.getLastName() == null ? "" : request.getLastName())).trim();
        String subject = request.getSubject() == null || request.getSubject().isBlank() ? "General" : request.getSubject();

        String body = "<h3>New contact form message</h3>"
                + "<p><b>Name:</b> " + esc(name) + "</p>"
                + "<p><b>Email:</b> " + esc(request.getEmail()) + "</p>"
                + "<p><b>Phone:</b> " + esc(request.getPhone()) + "</p>"
                + "<p><b>Subject:</b> " + esc(subject) + "</p>"
                + "<p><b>Message:</b><br>" + esc(request.getMessage()).replace("\n", "<br>") + "</p>";

        // If sending fails now, the outbox keeps it and retries later
        emailOutboxService.sendOrQueue(contactRecipient, "Website contact: " + subject + " - " + name, body, null, "CONTACT_FORM");
        return ResponseEntity.ok(new MessageResponse("Message received. We will get back to you soon."));
    }

    private static String esc(String value) {
        return value == null ? "" : HtmlUtils.htmlEscape(value);
    }
}
