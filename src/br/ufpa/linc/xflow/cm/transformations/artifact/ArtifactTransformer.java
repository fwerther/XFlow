package br.ufpa.linc.xflow.cm.transformations.artifact;

import java.util.HashSet;
import java.util.Set;

import br.ufpa.linc.xflow.cm.info.Artifact;
import br.ufpa.linc.xflow.data.dao.cm.FolderDAO;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Folder;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public abstract class ArtifactTransformer {

	protected Set<String> foldersPathCache;
	protected Entry processedEntry;
	
	
	public ArtifactTransformer() {
		//Empty constructor.
		foldersPathCache = new HashSet<String>();
	}
	
	
	public static ArtifactTransformer createInstance(boolean codeDownloadEnabled) {
		if(codeDownloadEnabled){
			return new SourceFileTransformer();
		} else {
			return new SimpleArtifactTransformer();
		}
	}
	
	abstract public ObjFile gatherArtifactInfo(final Artifact node) throws DatabaseException;
	
	public void gatherFolderInfo(final Artifact node) throws DatabaseException {
		if(node.getChangeType() == 'D'){
			Folder deletedFolder = new FolderDAO().findFolderByPath(processedEntry.getProject(), node.getTargetPath());
			if(deletedFolder != null){
				deletedFolder.setDeletedOn(processedEntry);
				new FolderDAO().update(deletedFolder);
			}
		}
	}

	public void setProcessedEntry(Entry processedEntry) {
		this.processedEntry = processedEntry;
	}
}
