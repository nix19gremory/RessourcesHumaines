/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet.besoin;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import model.gestionBesoin.Besoin;
import model.gestionBesoin.Task;
import model.gestionProfile.WantedProfile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 *
 * @author To Mamiarilaza
 */
@WebServlet(name = "AnnonceExportPDFServlet", urlPatterns = {"/annonce-export"})
public class AnnonceExportPDFServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=example.pdf");

        // Bien remplir ces données et tout doit aller automatiquement
        String logoPath = "/assets/images/entreprise_logo.png";
        String societyName = "HUILE DE BONGOLAVA";

        Besoin besoin = new Besoin();
        besoin.setDescription("Ces derniers temps le nombre de visiteur de notre page web a augmenté notre équipe soulève une grande charge et c'est pour cela qu'on vous cherche");

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(null, "Maintenance et réparation du serveur", 1));
        tasks.add(new Task(null, "Assurer le développement du site web", 1));
        tasks.add(new Task(null, "Assurer le développement du site web", 1));

        List<WantedProfile> wantedProfiles = new ArrayList<>();
        wantedProfiles.add(new WantedProfile("Développeur JAVA", null));
        wantedProfiles.add(new WantedProfile("Développeur React", null));
        
        String dateBesoin = "2023-01-05";
        String serviceName = "Informatique";
        

        String link = "http://www.huile-de-bongolava.mg/recrutement/postule";

        String societyContact = "034 21 561 26";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Ajout du logo dans la page
                PDImageXObject logo = PDImageXObject.createFromFile(getServletContext().getRealPath("/") + logoPath, document);
                contentStream.drawImage(logo, 50, 720, 80, 80);

                // Annonce de recrutement
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                writeText(contentStream, 65, 690, societyName + " recrute !");

                // Description du besoin
                int dynamicY = 660;     // pour que la hauteur s'adapte en fonction du nombres de ligne
                int lineHeight = 20;

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                int line = writeMultilineText(contentStream, 65, dynamicY, besoin.getDescription(), lineHeight);
                dynamicY -= lineHeight * (line + 1);

                contentStream.setLineWidth(1);
                contentStream.setStrokingColor(0, 0, 0);
                contentStream.moveTo(65, dynamicY);
                contentStream.lineTo(500, dynamicY);
                contentStream.stroke();
                dynamicY -= lineHeight;
                dynamicY -= lineHeight;

                // Liste des taches a faire
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                writeText(contentStream, 65, dynamicY, "Vos missions : ");
                dynamicY -= lineHeight;

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                for (Task task : tasks) {
                    writeText(contentStream, 65, dynamicY, "-  " + task.getTask());
                    dynamicY -= lineHeight;
                }

                // Liste des profil cherché
                dynamicY -= lineHeight;
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                writeText(contentStream, 65, dynamicY, "Intégrer notre équipe en étant :");
                dynamicY -= lineHeight;     // Saut à la ligne

                for (WantedProfile profile : wantedProfiles) {
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    writeText(contentStream, 65, dynamicY, "-  " + profile.getPoste() + " : ");
                    dynamicY -= lineHeight;

                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    // Info a propos du diplome
                    writeText(contentStream, 75, dynamicY, "-  Titulaire d'un " + "Master en MBDS");
                    dynamicY -= lineHeight;

                    // Info a propos de l'éxpérience
                    writeText(contentStream, 75, dynamicY, "-  Ayant plus de " + "2 ans d'éxpérience");
                    dynamicY -= lineHeight;
                    dynamicY -= lineHeight;
                }

                // Conclusion text
                dynamicY -= lineHeight;
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                String conclusion = "Veuillez postuler depuis notre site web, tout en envoyant les dossier justificative et un photo de vous en pièce jointe, voilà le lien";
                line = writeMultilineText(contentStream, 65, dynamicY, conclusion, lineHeight);
                dynamicY -= (line * lineHeight);

                // Link text
                dynamicY -= lineHeight;
                writeText(contentStream, 65, dynamicY, link);
                dynamicY -= lineHeight;

                // Renseignements
                dynamicY -= lineHeight;
                String renseignement = "Pour tout renseignement, conntactez nous : " + societyContact;
                writeText(contentStream, 65, dynamicY, renseignement);

                
            }
            
            // Exportation en image
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage image = pdfRenderer.renderImage(0);
            ImageIO.write(image, "PNG", new File(getServletContext().getRealPath("/annonces/" + dateBesoin + "_" + serviceName + "_" + "annonce.png")));

            // Exportation en pdf
            document.save(getServletContext().getRealPath("/annonces/" + dateBesoin + "_" + serviceName + "_" + "annonce.pdf"));
            
            // Affichage a l'écran
            document.save(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected static List<String> toMultilinesText(String text) {
        String[] multiwords = text.split(" ");      // Séparation par espaces

        List<String> stringListes = new ArrayList<>();

        int limit = 70;
        int carac = 0;
        String tempResult = "";

        for (String multiword : multiwords) {
            carac += multiword.length();
            if (carac > limit) {
                carac = 0;
                stringListes.add(tempResult);
                tempResult = "";
            } else {
                tempResult += " " + multiword;
            }
        }
        stringListes.add(tempResult);

        return stringListes;
    }

    protected static int writeMultilineText(PDPageContentStream contentStream, int x, int y, String text, int lineHeight) throws IOException {
        List<String> multilines = toMultilinesText(text);

        for (int i = 0; i < multilines.size(); i++) {
            writeText(contentStream, x, y - (lineHeight * i), multilines.get(i));
        }

        return multilines.size();
    }

    protected static void writeText(PDPageContentStream contentStream, int x, int y, String text) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
