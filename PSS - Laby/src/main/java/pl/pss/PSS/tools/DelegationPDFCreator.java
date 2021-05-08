package pl.pss.PSS.tools;

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import pl.pss.PSS.model.Delegation;

import org.apache.commons.io.IOUtils;

public class DelegationPDFCreator
{
    Delegation delegation;
    public DelegationPDFCreator(Delegation delegation) {
        this.delegation = delegation;
    }

    String getInUtf8(String s)
    {
        return new String(s.getBytes(StandardCharsets.UTF_8));
    }
    private void tableContent(PdfPTable table) throws IOException {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPadding(10);
        PdfPCell cellD = new PdfPCell();
        cellD.setBackgroundColor(Color.WHITE);
        cellD.setPadding(10);


        Font fontLeft = getArialUTF8();
        fontLeft.setColor(Color.BLACK);
        fontLeft.setSize(12);
        Font fontRight = getArialUTF8();
        fontRight.setColor(Color.BLACK);
        fontRight.setSize(10);


        cell.setPhrase(new Phrase("Delegation ID", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(String.valueOf(delegation.getDelegationId()), fontRight));
        table.addCell(cellD);


        cell.setPhrase(new Phrase("Description", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(delegation.getDescription(), fontRight));
        table.addCell(cellD);


        cell.setPhrase(new Phrase("Start Delegation", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(String.valueOf(delegation.getDateTimeStart()), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("End Delegation", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(String.valueOf(delegation.getDateTimeStop()), fontRight));
        table.addCell(cellD);


        cell.setPhrase(new Phrase("Travel diet amount", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(String.valueOf(delegation.getTravelDietAmount()), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("Breakfast Number", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(String.valueOf(delegation.getBreakfastNumber()), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("Dinner Number", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(String.valueOf(delegation.getDinnerNumber()), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("Supper Number", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(String.valueOf(delegation.getSupperNumber()), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("Transport Type", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(delegation.getTransportType().getTransportType(), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("Ticket Price", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(String.valueOf(delegation.getTicketPrice()), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("(If auto) Auto Capacity", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(delegation.getAutoCapacity().getAutoCapacity(), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("Kilometers", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(String.valueOf(delegation.getKm()), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("Accomodation Price", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(String.valueOf(delegation.getAccomodationPrice()), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("Other Tickets Price", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(String.valueOf(delegation.getOtherTicketsPrice()), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("Other Outlay Desc", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(String.valueOf(delegation.getOtherOutlayDesc()), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("Other Outlay Price", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(String.valueOf(delegation.getOtherOutlayPrice()), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("Person name", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(delegation.getDelegant().getName(), fontRight));
        table.addCell(cellD);

        cell.setPhrase(new Phrase("Person lastname", fontLeft));
        table.addCell(cell);
        cellD.setPhrase(new Phrase(delegation.getDelegant().getLastName(), fontRight));
        table.addCell(cellD);

    }

    Font getArialUTF8() throws IOException {
        byte[] fontByte = IOUtils
                .toByteArray(this.getClass().getClassLoader().getResourceAsStream("fonts/arial.ttf"));
        BaseFont baseFont = BaseFont.createFont("arial.ttf", BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED, true, fontByte, null);
        Font font = new Font(baseFont,10,Font.NORMAL,Color.BLACK);
        return font;
    }
    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
//
        Font font = getArialUTF8();

        font.setSize(18);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("Delegation information", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        PdfPTable table = new PdfPTable(2);

        table.setWidthPercentage(100f);
        //table.setWidths(new float[] {1.5f, 3.5f, 3.0f, 3.0f, 1.5f});
        table.setSpacingBefore(10);

        tableContent(table);

        document.add(table);


        document.close();

    }
    public void print(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        document.setJavaScript_onLoad("window.print()");
        PdfWriter pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        PdfAction action = new PdfAction(PdfAction.PRINTDIALOG);
        pdfWriter.setOpenAction(action);
//
        Font font = getArialUTF8();

        font.setSize(18);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("Delegation information", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        PdfPTable table = new PdfPTable(2);

        table.setWidthPercentage(100f);
        //table.setWidths(new float[] {1.5f, 3.5f, 3.0f, 3.0f, 1.5f});
        table.setSpacingBefore(10);

        tableContent(table);

        document.add(table);


        document.close();

    }
}
