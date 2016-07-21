
package gov.loc.repository.bagit.domain;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import gov.loc.repository.bagit.hash.SupportedAlgorithm;

/**
 * A manifest is a list of files and their corresponding checksum with the algorithm used to generate that checksum
 */
public class Manifest {
  private final SupportedAlgorithm algorithm;
  private Map<Path, String> fileToChecksumMap = new HashMap<>();
  
  public Manifest(final SupportedAlgorithm algorithm){
    this.algorithm = algorithm;
  }

  public Map<Path, String> getFileToChecksumMap() {
    return fileToChecksumMap;
  }

  public void setFileToChecksumMap(final Map<Path, String> fileToChecksumMap) {
    this.fileToChecksumMap = fileToChecksumMap;
  }

  public SupportedAlgorithm getAlgorithm() {
    return algorithm;
  }

  @Override
  public String toString() {
    return "Manifest [algorithm=" + algorithm + ", fileToChecksumMap=" + fileToChecksumMap + "]";
  }
}
