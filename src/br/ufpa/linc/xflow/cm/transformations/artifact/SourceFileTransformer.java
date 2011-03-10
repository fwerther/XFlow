package br.ufpa.linc.xflow.cm.transformations.artifact;

import br.ufpa.linc.xflow.cm.info.Artifact;
import br.ufpa.linc.xflow.cm.transformations.loc.LOCProcessor;
import br.ufpa.linc.xflow.data.dao.cm.ObjFileDAO;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.util.FileUtil;

public class SourceFileTransformer extends ArtifactTransformer {

	public SourceFileTransformer() {
		// Empty constructor.
		super();
	}
	
	@Override
	public ObjFile gatherArtifactInfo(Artifact node) throws DatabaseException {

		switch (node.getChangeType()) {

		case 'R':

		case 'A':
			ObjFile addedFile = new ObjFile();
			addedFile.setPath(node.getTargetPath());
			addedFile.setEntry(processedEntry);
			addedFile.setOperationType('A');
			addedFile.setSourceCode(node.getSourceCode());
			FileUtil.extractNameAndExtension(addedFile);
			FileUtil.buildFilePath(processedEntry.getProject(), addedFile);
			LOCProcessor.extractCodeInfo(addedFile);
			foldersPathCache.add(addedFile.getPath());
			return addedFile;

		case 'M':

			ObjFile file;
			boolean newFileFlag = true;
			if(foldersPathCache.contains(node.getTargetPath())){
				newFileFlag = false;
			}
			else{
				file = FileUtil.checkFile(processedEntry.getProject().getId(), node.getTargetPath());
				if(file != null){
					newFileFlag = false;
				}
			}
			
			if(newFileFlag){
				file = new ObjFile();
				file.setPath(node.getTargetPath());
				file.setOperationType('A');
				file.setEntry(processedEntry);
				file.setSourceCode(node.getSourceCode());
				FileUtil.extractNameAndExtension(file);
				FileUtil.buildFilePath(processedEntry.getProject(), file);
				LOCProcessor.extractCodeInfo(file);
				foldersPathCache.add(file.getPath());
				return file;
			} else {
				file = new ObjFile();
				file.setPath(node.getTargetPath());
				file.setOperationType('M');
				file.setSourceCode(node.getSourceCode());
				file.setDiffCode(node.getDiffCode());
				file.setEntry(processedEntry);
				FileUtil.extractNameAndExtension(file);
				LOCProcessor.extractCodeInfo(file);
				return file;
			}

		case 'D':
			ObjFile deletedFile = new ObjFile();
			ObjFile existingFile = FileUtil.checkFile(processedEntry.getProject().getId(), node.getTargetPath());
			if(existingFile != null){
				existingFile.setDeletedOn(processedEntry);
				new ObjFileDAO().update(existingFile);
			}
			deletedFile.setPath(node.getTargetPath());
			deletedFile.setOperationType('D');
			deletedFile.setEntry(processedEntry);
			FileUtil.extractNameAndExtension(deletedFile);
			return deletedFile;

		default:
			// Never reached case.
			return null;
		}
	}
}
