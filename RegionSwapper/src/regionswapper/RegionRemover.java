package regionswapper;


import java.util.ArrayList;
import java.util.List;

import javax.swing.JSpinner;

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
import com.biomatters.geneious.publicapi.plugin.Options.IntegerOption;
import com.biomatters.geneious.publicapi.plugin.SequenceAnnotationGenerator;
import com.biomatters.geneious.publicapi.plugin.SequenceAnnotationGenerator.AnnotationGeneratorResult.ResidueAdjustment;
import com.biomatters.geneious.publicapi.utilities.Interval;

public class RegionRemover extends SequenceAnnotationGenerator{
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
	    final Options options = new Options(getClass());
		
		NucleotideSequenceDocument document = (NucleotideSequenceDocument) documents[0].getDocument();
		  				
		int maxSequenceLength = document.getSequenceLength();
		int start = selectionRange.getFirstSelectedResidue();
		int end = selectionRange.getLastSelectedResidue();

		options.addCustomOption(new SelectionGrabOption("selectionGrabber", new Interval(start, end)));
		options.getOption("selectionGrabber").addChangeListener(new SimpleListener(){
			public void objectChanged(){
				Interval interval = (Interval) options.getOption("selectionGrabber").getValue();
				IntegerOption start = (IntegerOption) options.getOption("selectionStart");
				start.setValue(interval.getMin());
				//TODO: Verify: Inclusive or Exclusive?
				IntegerOption end = (IntegerOption) options.getOption("selectionEnd");
				end.setValue(interval.getMaxExclusive());
			}
		});

		options.addIntegerOption("selectionStart","Region Start",start,0,maxSequenceLength);
		options.addIntegerOption("selectionEnd","Region End",end,0,maxSequenceLength);
		
		//reset to the current selection
		options.getOption("selectionStart").restoreDefault();
		options.getOption("selectionEnd").restoreDefault();
		
		JSpinner startComponent = (JSpinner) options.getOption("selectionStart").getComponent();
		startComponent.setValue(start);
		JSpinner endComponent = (JSpinner) options.getOption("selectionEnd").getComponent();
		endComponent.setValue(end);
		
		return options;
	}

	public List<AnnotationGeneratorResult> generate(AnnotatedPluginDocument[] documentList, SelectionRange selectionRange, ProgressListener progressListener, Options options) throws DocumentOperationException {
		Options regionRemoverOptions= getOptions(documentList, selectionRange); // Since an instance of MyOptions is always returned from getOptions, this cast is always valid.
	    
		List<AnnotationGeneratorResult> results = new ArrayList<AnnotationGeneratorResult>();
		
		int start = (Integer) regionRemoverOptions.getOption("selectionStart").getValue();
		int end = (Integer) regionRemoverOptions.getOption("selectionEnd").getValue();
		
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
