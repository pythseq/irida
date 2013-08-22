package ca.corefacility.bioinformatics.irida.processing.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;

import com.google.common.collect.ImmutableMap;

/**
 * Handle gzip-ed files (if necessary). This class partially assumes that gzip
 * compressed files have the extension ".gz" (not for determining whether or not
 * the file is compressed, but rather for naming the decompressed file). If the
 * compressed file does not end with ".gz", then it will be renamed as such so
 * that the decompressed file name will not conflict with the compressed file
 * name.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class GzipFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(GzipFileProcessor.class);
	private static final String GZIP_EXTENSION = ".gz";

	private SequenceFileRepository sequenceFileRepository;

	public GzipFileProcessor(SequenceFileRepository sequenceFileRepository) {
		this.sequenceFileRepository = sequenceFileRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceFile process(SequenceFile sequenceFile) throws FileProcessorException {
		Path file = sequenceFile.getFile();
		String nameWithoutExtension = file.toString();
		String originalFilename = file.toString();

		// strip the extension from the filename (if necessary)
		if (nameWithoutExtension.endsWith(GZIP_EXTENSION)) {
			nameWithoutExtension = nameWithoutExtension.substring(0, nameWithoutExtension.lastIndexOf(GZIP_EXTENSION));
		}

		try {
			file = addExtensionToFilename(file, GZIP_EXTENSION);
			GZIPInputStream zippedInputStream = new GZIPInputStream(Files.newInputStream(file));

			logger.debug("Handling gzip compressed file.");

			Path target = Paths.get(nameWithoutExtension);
			logger.debug("Writing uncompressed file to [" + target + "]");

			Files.copy(zippedInputStream, target);

			// if the new name is different from the name before, update the
			// file name in the database.
			if (!nameWithoutExtension.equals(originalFilename)) {
				sequenceFile = sequenceFileRepository.update(sequenceFile.getId(),
						ImmutableMap.of("file", (Object) target));
			}
		} catch (ZipException e) {
			// the file isn't gzip formatted, so just proceed; don't throw an
			// exception, don't process the file.
		} catch (Exception e) {
			logger.debug("Failed to process the input file [" + sequenceFile + "]; stack trace follows.", e);
			throw new FileProcessorException("Failed to process input file [" + sequenceFile + "].");
		}

		return sequenceFile;
	}

	/**
	 * Ensures that the supplied file ends with a specific extension.
	 * 
	 * @param file
	 *            the file to handle.
	 * @return the modified (or not) file.
	 */
	private Path addExtensionToFilename(Path file, String extension) throws IOException {
		String currentName = file.toString();
		if (!currentName.endsWith(extension)) {
			String modifiedName = new StringBuilder(currentName).append(extension).toString();
			Path target = Paths.get(modifiedName);
			file = Files.move(file, target);
		}

		return file;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean modifiesFile() {
		return true;
	}
}