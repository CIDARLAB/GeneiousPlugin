package regionswapper;


import java.util.ArrayList;
import java.util.List;

import jebl.util.ProgressListener;

import org.virion.jam.util.SimpleListener;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotation;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotationInterval;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.plugin.SequenceAnnotationGenerator;
import com.biomatters.geneious.publicapi.plugin.SequenceAnnotationGenerator.AnnotationGeneratorResult.ResidueAdjustment;
import com.biomatters.geneious.publicapi.utilities.Interval;

public class RegionRemover extends SequenceAnnotationGenerator{
	//private DefaultNucleotideSequence sequence;
	//private final Integer maxSequenceLength;
	
   
	private class RegionRemoverOptions extends Options{
    	private final IntegerOption selectionStart;
    	private final IntegerOption selectionEnd;
    	private final SelectionGrabOption selectionGrabber;
    	private final Integer maxSequenceLength;
    	
    	public RegionRemoverOptions(final AnnotatedPluginDocument[] documentList, final SelectionRange selectionRange) throws DocumentOperationException{
    		//maxSequenceLength = getInternalSequenceLength();
    		
    		NucleotideSequenceDocument document = (NucleotideSequenceDocument) documentList[0].getDocument();
    		//DocumentSelectionSignature selection = DocumentSelectionSignature.forNucleotideSequences(1, 1);
    		//DocumentSelectionSignature selection = getSelectionSignatures()[0];// always only 1
    		  				
    		maxSequenceLength = document.getSequenceLength();
    		//int start = selection.getNucleotideSequenceMinimum();
    		int start = selectionRange.getFirstSelectedSequence();
    		int end = selectionRange.getLastSelectedSequence();
    		
    		//selectionGrabber = addCustomOption(new SelectionGrabOption("selectionGrabber", new Interval(1,maxSequenceLength)));
    		selectionGrabber = addCustomOption(new SelectionGrabOption("selectionGrabber", new Interval(start, end)));
    		/*selectionGrabber.addChangeListener(new SimpleListener(){
    			public void objectChanged(){
    				selectionStart.setValue(selectionGrabber.getValue().getMin());
    				//TODO: Verify: Inclusive or Exclusive?
    				selectionEnd.setValue(selectionGrabber.getValue().getMaxExclusive());
    			}
    		});*/
    		
    		//try{
    			start = selectionGrabber.getValue().getMin();
    			end = selectionGrabber.getValue().getMaxExclusive();
    		//}
    		//catch(NullPointerException e){};
    		
    		selectionStart = addIntegerOption("selectionStart","Region Start",start);
    		selectionEnd = addIntegerOption("selectionEnd","Region End",end);
    		
    	}
    	public int getStart(){
    		return selectionStart.getValue();
    	}
    	public int getEnd(){
    		return selectionEnd.getValue();
    	}
    	
    }

	//set the location of the action
	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Delete a Region", 
				"Remove a region from the active nucleotide sequence").setMainMenuLocation(GeneiousActionOptions.MainMenu.Sequence);
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public DocumentSelectionSignature[] getSelectionSignatures() {
        return new DocumentSelectionSignature[] {
                new DocumentSelectionSignature(NucleotideSequenceDocument.class,1,1)
                // This indicates this annotation generator will accept a single nucleotide sequence as input
        };
    }

	public Options getOptions(final AnnotatedPluginDocument[] documents, final SelectionRange selectionRange) throws DocumentOperationException {
	    return new RegionRemoverOptions(documents, selectionRange);
	}

	public List<AnnotationGeneratorResult> generate(AnnotatedPluginDocument[] documentList, SelectionRange selectionRange, ProgressListener progressListener, Options options) throws DocumentOperationException {
		//NucleotideSequenceDocument document= (NucleotideSequenceDocument) documentList[0].getDocument(); 
		RegionRemoverOptions regionRemoverOptions= (RegionRemoverOptions) getOptions(documentList, selectionRange); // Since an instance of MyOptions is always returned from getOptions, this cast is always valid.
	     //boolean sampleOptionSelected = myOptions.isSampleOption();
		
		List<AnnotationGeneratorResult> results = new ArrayList<AnnotationGeneratorResult>();
		
		int start = regionRemoverOptions.getStart() -1;//convert to 0 indexed
		int end = (Integer) regionRemoverOptions.getEnd() -1;
		
		//create the new sequence with the deletion, as a subclass of AnnotationGeneratorResult
				ResidueAdjustment adjustment = new ResidueAdjustment(start,end,"");
		
		//add the new deletion site annotation
		SequenceAnnotation annotation = new SequenceAnnotation("Deletion Site", "deletion",
				new SequenceAnnotationInterval(start,start));
		
		AnnotationGeneratorResult result = new AnnotationGeneratorResult();
		result.addResidueAdjustment(adjustment);
		result.addAnnotationToAdd(annotation);
		
		results.add(result);
		return results;
	     
	}

}
