package br.com.ifba.api.service;

import br.com.ifba.ingresso.entity.Ingresso;
import br.com.ifba.pagamento.entity.Pedido;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailIService {

    private final JavaMailSender mailSender;

    @Override
    public void sendTickets(Pedido pedido, List<Ingresso> ingressos) {
        String destinatario = pedido.getUsuario().getPessoa().getEmail();
        String assunto = "🎟️ Seus Ingressos - Cine The Golden (Pedido #" + pedido.getId() + ")";

        try {
            // Monta o HTML do e-mail
            String htmlContent = buildHtmlTickets(pedido, ingressos);

            // Envia
            sendHtmlEmail(destinatario, assunto, htmlContent);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar e-mail de ingressos.");
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("noreply@cinegolden.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = ativa HTML

        mailSender.send(message);
    }

    // Método auxiliar para criar um template HTML bonito
    private String buildHtmlTickets(Pedido pedido, List<Ingresso> ingressos) {
        StringBuilder sb = new StringBuilder();

        // Estilos CSS inline (para garantir que funcione no Gmail/Outlook)
        sb.append("<html><body style='font-family: Arial, sans-serif; color: #333;'>");
        sb.append("<div style='background-color: #f8f9fa; padding: 20px;'>");

        sb.append("<div style='background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); max-width: 600px; margin: 0 auto;'>");

        sb.append("<h2 style='color: #d4af37; text-align: center;'>🎬 Cine The Golden</h2>");
        sb.append("<p>Olá, <strong>").append(pedido.getUsuario().getPessoa().getEmail()).append("</strong>!</p>");
        sb.append("<p>O pagamento do seu pedido <strong>#").append(pedido.getId()).append("</strong> foi confirmado.</p>");
        sb.append("<p>Aqui estão os seus ingressos:</p>");

        // Loop pelos ingressos
        for (Ingresso ingresso : ingressos) {
            sb.append("<div style='border: 1px solid #ddd; padding: 15px; margin-bottom: 10px; border-radius: 5px; border-left: 5px solid #d4af37;'>");
            sb.append("<h3 style='margin: 0 0 10px 0;'>").append(ingresso.getSessao().getFilme().getTitulo()).append("</h3>");

            String dataFormatada = ingresso.getSessao().getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            sb.append("<p style='margin: 5px 0;'><strong>📅 Data:</strong> ").append(dataFormatada)
                    .append(" às ").append(ingresso.getSessao().getHorario()).append("</p>");

            sb.append("<p style='margin: 5px 0;'><strong>💺 Poltrona:</strong> ").append(ingresso.getCodigoPoltrona()).append("</p>");
            sb.append("<p style='margin: 5px 0;'><strong>📍 Sala:</strong> ").append(ingresso.getSessao().getSala().getNome()).append("</p>");
            sb.append("</div>");
        }

        sb.append("<hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>");
        sb.append("<p style='font-size: 12px; color: #777; text-align: center;'>Apresente este e-mail na entrada do cinema.</p>");
        sb.append("</div></div></body></html>");

        return sb.toString();
    }
}
