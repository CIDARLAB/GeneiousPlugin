package regionswapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jebl.util.ProgressListener;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.AminoAcidSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.*;

/**
 * Based on the ReverseSeqPlugin by Joseph Heled
 * 
 * @author Michael Quintin
 * @version $Id: RemoveRegionPlugin.java 1 2014-11-24 $
 */
public class GeneInserterPlugin extends GeneiousPlugin {
    public SequenceAnnotationGenerator[] getSequenceAnnotationGenerators() {
        return new SequenceAnnotationGenerator[]{
                new GeneInserter()
        };
    }
    
    public String getName() {
        return "Insert a Sequence";
    }

    public String getDescription() {
        return "Select and insert a sequence to insert into the active sequence";
    }

    public String getHelp() {
        return null;
    }

    public String getAuthors() {
        return "Michael Quintin (mquintin@bu.edu)";
    }

    public String getVersion() {
        return "0.1";
    }

    public DocumentOperation[] getDocumentOperations() {
      return new DocumentOperation[]{insertSequence};
    }

    private DocumentOperation insertSequence = new DocumentOperation() {
    	
    	@Override
        public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] documents, ProgressListener progressListener, Options options) throws DocumentOperationException {
    		final AnnotatedPluginDocument doc = documents[0];

            final SequenceDocument sequenceDoc = (SequenceDocument) doc.getDocument();

            if( sequenceDoc instanceof NucleotideSequenceDocument) {
            	
            } else if( sequenceDoc instanceof AminoAcidSequenceDocument) {
            	//TODO: Handle this
                throw new DocumentOperationException("Expected a nucleotide sequence");
            } else {
                throw new DocumentOperationException("unexpected sequence");
            }
            List<PluginDocument> results = new ArrayList<PluginDocument>();
            //TODO: change to do work here
            return DocumentUtilities.createAnnotatedPluginDocuments(results);
	
    		
    	}

        public GeneiousActionOptions getActionOptions() {
            return new GeneiousActionOptions("Insert a Sequence", "Select and insert a sequence to insert into the active sequence",
                    null, GeneiousActionOptions.Category.None).setInMainToolbar(true);
        }

        public String getHelp() {
            return "Select and insert a sequence to insert into the active sequence";
        }

        public DocumentSelectionSignature[] getSelectionSignatures() {
            DocumentSelectionSignature singleSequenceSignature =
                    new DocumentSelectionSignature(SequenceDocument.class, 1, 1);

            return new DocumentSelectionSignature[]{singleSequenceSignature};
        }

        @Override
        public Options getOptions(final AnnotatedPluginDocument... documents) {
            return null;
        }
    };

    public String getMinimumApiVersion() {
        return "4.0";
    }

    public int getMaximumApiVersion() {
        return 4;
    }
    

}
