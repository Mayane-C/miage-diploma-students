package fr.pantheonsorbonne.miage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

class EncryptedDiplomaGeneratorTest extends DiplomaGeneratorTest {

	@Test
	void testEncryptedPdfTest() throws IOException, DocumentException {

		Student stu = new Student(1, "Nicolas", "M.", "nico");
		DiplomaGenerator generator = new MiageDiplomaGenerator(stu, DiplomaGeneratorTest.currentDate);
		AbstractDiplomaGenerator encryptedGenerator = new EncryptedDiplomaGeneratorDecorator(generator, "abc");
		FileGenerator<AbstractDiplomaGenerator> adapter = new DiplomaFileAdapter(encryptedGenerator);

		Path tempFileEncrypted = Files.createTempFile("prefix", ".pdf");
		Path tempFileDecrypted = Files.createTempFile("prefix", ".pdf");

		adapter.generateFile(tempFileEncrypted.toString());

		PdfReader reader = new PdfReader(tempFileEncrypted.toString(), "abc".getBytes());

		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(tempFileDecrypted.toString()));
		stamper.close();
		reader.close();

		ByteArrayOutputStream generatedImageData = new ByteArrayOutputStream();

		System.out.println(tempFileDecrypted);

		ByteArrayOutputStream referenceImageData = new ByteArrayOutputStream();

		writePDFImageRasterBytes(tempFileDecrypted.toFile(), generatedImageData);
		writePDFImageRasterBytes(new File("src/test/resources/nicolas.pdf"), referenceImageData);

		assertArrayEquals(referenceImageData.toByteArray(), generatedImageData.toByteArray());

		stamper.close();
		reader.close();

	}
}