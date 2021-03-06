package gov.loc.repository.bagit.verify;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple task to check if a file exists on the filesystem. This is thread safe, so many can be called at once.
 */
@SuppressWarnings(value = {"PMD.DoNotUseThreads"})
public class CheckIfFileExistsTask implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(CheckIfFileExistsTask.class);
  private transient final Path file;
  //TODO if performance is an issue look at concurrentHashMap - it will take up more space but insertion is O(1) vs O(n)
  private transient final Set<Path> missingFiles;
  private transient final CountDownLatch latch;
  
  public CheckIfFileExistsTask(final Path file, final Set<Path> missingFiles, final CountDownLatch latch) {
    this.file = file;
    this.latch = latch;
    this.missingFiles = missingFiles;
  }

  @Override
  public void run() {
    final boolean existsNormalized = existsNormalized();
    final boolean fileExists = Files.exists(file);
    
    if(!fileExists){
      if(existsNormalized){
        logger.warn("File name [{}] has a different normalization than what is contained on the filesystem!", file);
      }
      else{
        missingFiles.add(file);
      }
    }
    
    latch.countDown();
  }
  
  /**
   * if a file is parially normalized or of a different normalization then the manifest specifies it will fail the existence test.
   * This method checks for that by normalizing what is on disk with the normalized filename and see if they match.
   * 
   * @return true if the normalized filename matches one on disk in the specified folder
   */
  private boolean existsNormalized(){
    final String normalizedFile = Normalizer.normalize(file.toString(), Normalizer.Form.NFD);
    final Path parent = file.getParent();
    if(parent != null){
      try(final DirectoryStream<Path> files = Files.newDirectoryStream(parent)){
        for(final Path fileToCheck : files){
          final String normalizedFileToCheck = Normalizer.normalize(fileToCheck.toString(), Normalizer.Form.NFD);
          if(normalizedFile.equals(normalizedFileToCheck)){
            return true;
          }
        }
      }
      catch(IOException e){
        logger.error("Error while trying to read [{}] to see if any files in that directory match the normalized "
            + "filename of [{}]", parent, normalizedFile, e);
      }
    }
    
    return false;
  }
}
