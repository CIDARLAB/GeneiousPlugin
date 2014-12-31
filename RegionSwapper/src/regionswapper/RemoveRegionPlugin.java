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
public class RemoveRegionPlugin extends GeneiousPlugin {
    public SequenceAnnotationGenerator[] getSequenceAnnotationGenerators() {
        return new SequenceAnnotationGenerator[]{
                new RegionRemover()
        };
    }
    
    public String getName() {
        return "Remove Annotated Region";
    }

    public String getDescription() {
        return "Remove an annotated region from the active sequence, leaving a new placeholder annotation behind";
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
      return new DocumentOperation[]{removeRegion};
    }

    private DocumentOperation removeRegion = new DocumentOperation() {
    	
    	@Override
        public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] documents, ProgressListener progressListener, Options options) throws DocumentOperationException {
    		final AnnotatedPluginDocument doc = documents[0];

            final SequenceDocument sequenceDoc = (SequenceDocument) doc.getDocument();
            String seq = sequenceDoc.getSequenceString();

            //StringBuilder sb = new StringBuilder(seq);
            
            
            final String name = doc.getName();
            final String description = sequenceDoc.getDescription();

            final Date date = new Date();

            //PluginDocument newDoc;

            if( sequenceDoc instanceof NucleotideSequenceDocument) {
            	
               //reversedDoc = new DefaultNucleotideSequence(rname, rdescription, reversed, date);
            } else if( sequenceDoc instanceof AminoAcidSequenceDocument) {
            	//TODO: Handle this
                throw new DocumentOperationException("Expected a nucleotide sequence");
            	//reversedDoc = new DefaultAminoAcidSequence(rname, rdescription, reversed, date);
            } else {
                throw new DocumentOperationException("unexpected sequence");
            }
            List<PluginDocument> results = new ArrayList<PluginDocument>();
            //results.add(newDoc);
            
            //TODO: return what?
            return DocumentUtilities.createAnnotatedPluginDocuments(results);
	
    		
    	}

    	/*
    	//TODO: Rewrite all this code
        @Override
        public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] documents, ProgressListener progressListener, Options options) throws DocumentOperationException {
            final AnnotatedPluginDocument doc = documents[0];

            final SequenceDocument sequenceDoc = (SequenceDocument) doc.getDocument();
            String seq = sequenceDoc.getSequenceString();

            StringBuilder sb = new StringBuilder(seq);

            final String name = doc.getName();
            final String description = sequenceDoc.getDescription();

            final Date date = new Date();

            PluginDocument newDoc;

            if( sequenceDoc instanceof NucleotideSequenceDocument) {
            	newDoc = RegionRemover.
               //reversedDoc = new DefaultNucleotideSequence(rname, rdescription, reversed, date);
            } else if( sequenceDoc instanceof AminoAcidSequenceDocument) {
            	//TODO: Handle this
                throw new DocumentOperationException("Expected a nucleotide sequence");
            	//reversedDoc = new DefaultAminoAcidSequence(rname, rdescription, reversed, date);
            } else {
                throw new DocumentOperationException("unexpected sequence");
            }
            List<PluginDocument> results = new ArrayList<PluginDocument>();
            results.add(newDoc);
            
            //TODO: return what?
            return DocumentUtilities.createAnnotatedPluginDocuments(results);
        } */

        public GeneiousActionOptions getActionOptions() {
            return new GeneiousActionOptions("Remove Region", "Delete a region from the selected sequence",
                    null, GeneiousActionOptions.Category.None).setInMainToolbar(true);
        }

        public String getHelp() {
            return "Removes a specific region from the selected sequence, and adds a new annotation to mark the deletion site.";
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
