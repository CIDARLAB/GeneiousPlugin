package regionswapper;


import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JSpinner;

import jebl.util.ProgressListener;

import org.virion.jam.util.SimpleListener;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseService;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotation;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotationInterval;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.plugin.Options.FileSelectionOption;
import com.biomatters.geneious.publicapi.plugin.Options.IntegerOption;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;
import com.biomatters.geneious.publicapi.plugin.SequenceAnnotationGenerator;
import com.biomatters.geneious.publicapi.plugin.SequenceAnnotationGenerator.AnnotationGeneratorResult.ResidueAdjustment;
import com.biomatters.geneious.publicapi.utilities.Interval;

//functions to design:
//select a region. then select a list of sequences. ennumerate a set of sequences that replace the region with each
//do that for multiple regions. enumerate all possibilities

public class GeneInserter extends SequenceAnnotationGenerator{
		//set the location of the action
	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Insert a Sequence", 
				"Insert a sequence into the active nucleotide sequence").setMainMenuLocation(GeneiousActionOptions.MainMenu.Sequence);
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

	//TODO: Create options here
	//for now, create the option to select a gene as a file, then insert it at the selected location
	
	public Options getOptions(final AnnotatedPluginDocument[] documents, final SelectionRange selectionRange) throws DocumentOperationException {
	    final Options options = new Options(getClass());
		
		NucleotideSequenceDocument document = (NucleotideSequenceDocument) documents[0].getDocument();
		  				
		int maxSequenceLength = document.getSequenceLength();
		int start = selectionRange.getFirstSelectedResidue();
		//int end = selectionRange.getLastSelectedResidue();

		/*options.addCustomOption(new SelectionGrabOption("selectionGrabber", new Interval(start, end)));
		options.getOption("selectionGrabber").addChangeListener(new SimpleListener(){
			public void objectChanged(){
				Interval interval = (Interval) options.getOption("selectionGrabber").getValue();
				IntegerOption start = (IntegerOption) options.getOption("selectionStart");
				start.setValue(interval.getMin());
				//TODO: Verify: Inclusive or Exclusive?
				IntegerOption end = (IntegerOption) options.getOption("selectionEnd");
				end.setValue(interval.getMaxExclusive());
			}
		});*/
		

		options.addIntegerOption("selectionStart","Insertion Location",start,0,maxSequenceLength);
		//options.addIntegerOption("selectionEnd","Region End",end,0,maxSequenceLength);
		
		//reset to the current selection
		options.getOption("selectionStart").restoreDefault();
		//options.getOption("selectionEnd").restoreDefault();
		
		JSpinner startComponent = (JSpinner) options.getOption("selectionStart").getComponent();
		startComponent.setValue(start);
		
		options.addFileSelectionOption("fileSelect", "Select a sequence to insert", "");
		((FileSelectionOption) options.getOption("fileSelect")).setSelectionType(JFileChooser.FILES_ONLY);
		
		return options;
	}

	//TODO: Do the work here
	public List<AnnotationGeneratorResult> generate(AnnotatedPluginDocument[] documentList, SelectionRange selectionRange, ProgressListener progressListener, Options options) throws DocumentOperationException {
		Options inserterOptions= getOptions(documentList, selectionRange); // Since an instance of MyOptions is always returned from getOptions, this cast is always valid.
	    SequenceDocument targetDoc = (SequenceDocument) documentList[0]; //TODO: do this better
		
		List<AnnotationGeneratorResult> results = new ArrayList<AnnotationGeneratorResult>();
		
		int start = (Integer) inserterOptions.getOption("selectionStart").getValue();
		
		DatabaseService database= (DatabaseService) PluginUtilities.getGeneiousService("LocalDocuments");
		String file = inserterOptions.getOption("fileSelect").getValueAsString();
		List<AnnotatedPluginDocument> list = database.retrieve(file);
		SequenceDocument insertDoc = (SequenceDocument) list.get(0).getDocument();
		
		//insert the sequence
		ResidueAdjustment adjustment = new ResidueAdjustment(start, start, insertDoc.getSequenceString());
		
		//add the new site annotation
		SequenceAnnotation insertAnnotation = new SequenceAnnotation("Inserted Sequence", "insertion",
				new SequenceAnnotationInterval(start, start + insertDoc.getSequenceLength()));
		
		AnnotationGeneratorResult result = new AnnotationGeneratorResult();
		result.addResidueAdjustment(adjustment);
		result.addAnnotationToAdd(insertAnnotation);
		
		//adjust existing annotations that span the insertion
		for (SequenceAnnotation a : targetDoc.getSequenceAnnotations()){
			result.addAnnotationToRemove(a);
			SequenceAnnotation newAnnotation = a.getAnnotationAdjustedForInsertion(start+1, insertDoc.getSequenceLength(), false, targetDoc.getSequenceLength() + insertDoc.getSequenceLength());
			result.addAnnotationToAdd(newAnnotation);
		}
		
		//copy all the annotations from the inserted sequence
				List<SequenceAnnotation> annotations = insertDoc.getSequenceAnnotations();
				for (SequenceAnnotation a : annotations){
					List<SequenceAnnotationInterval> ints = new ArrayList<SequenceAnnotationInterval>();
					for (SequenceAnnotationInterval i : a.getIntervals()){
						ints.add(i.offsetBy(start));
					}
					a.setIntervals(ints);
					result.addAnnotationToAdd(a);
				}
		
		results.add(result);
		return results;
	    	}

}
