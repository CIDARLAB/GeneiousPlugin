package regionswapper;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JSpinner;

import jebl.util.ProgressListener;

import org.virion.jam.util.SimpleListener;

import com.biomatters.geneious.publicapi.components.OptionsPanel;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotation;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotationInterval;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.plugin.Options.FileSelectionOption;
import com.biomatters.geneious.publicapi.plugin.Options.ButtonOption;
import com.biomatters.geneious.publicapi.plugin.Options.IntegerOption;
import com.biomatters.geneious.publicapi.plugin.Options.Option;
import com.biomatters.geneious.publicapi.plugin.Options.RadioOption;
import com.biomatters.geneious.publicapi.plugin.Options.OptionValue;
import com.biomatters.geneious.publicapi.plugin.SequenceAnnotationGenerator;
import com.biomatters.geneious.publicapi.plugin.SequenceAnnotationGenerator.AnnotationGeneratorResult.ResidueAdjustment;
import com.biomatters.geneious.publicapi.utilities.Interval;

public class Enumerator extends SequenceAnnotationGenerator{
	
	static int optionTabs = 1; //used to name child tabs in the options pane
	
		//set the location of the action
	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Enumerate Gene Replacements", 
				"Generate a set of sequences with selected genes replaced").setMainMenuLocation(GeneiousActionOptions.MainMenu.Sequence);
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public DocumentSelectionSignature[] getSelectionSignatures() {
        return new DocumentSelectionSignature[] {
                new DocumentSelectionSignature(SequenceDocument.class,1,1)
                // This indicates this annotation generator will accept a single nucleotide sequence as input
        };
    }

	public Options getOptions(final AnnotatedPluginDocument[] documents, final SelectionRange selectionRange) throws DocumentOperationException {
	    //TODO: consider redoing this with nested addMultipleOptions instead of child options
		
		final Options options = new Options(getClass());
		/*
		SequenceDocument document = (SequenceDocument) documents[0].getDocument();
		  				
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
		*/
	    options.addChildOptions(String.valueOf(optionTabs), "Target " + optionTabs, "", createChildOptions(documents, selectionRange));
	    optionTabs++;
	    options.addChildOptionsPageChooser("childOptionsPageChooser", "Sites to enumerate", new ArrayList<String>(), Options.PageChooserType.TABBED_PANE, true);
	    options.addButtonOption("addButon","","Add additional target");
	    options.addButtonOption("removeButton","","Remove target");
	    
		((ButtonOption) options.getOption("addButton")).addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					options.addChildOptions(String.valueOf(optionTabs), "Target " + optionTabs, "", createChildOptions(documents, selectionRange));
					optionTabs++;
				} catch (DocumentOperationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
	    
		((ButtonOption) options.getOption("removeButton")).addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Option chooser = options.getOption("childOptionsPageChooser");
				String activeChildName = chooser.getValueAsString();
				//TODO: complete from here
			}
			
		});
		
		return options;
	}
	
	private Options createChildOptions(AnnotatedPluginDocument[] documents, SelectionRange selectionRange) throws DocumentOperationException{
		final Options child = new Options(getClass());
		OptionValue[] radioValues = {new Options.OptionValue("Insertion","Insertion"), new Options.OptionValue("Replacement","Replacement")};
		final ArrayList<String> files = new ArrayList<String>();
		
		child.addRadioOption("replacementType", "Replacement Type", radioValues, radioValues[0], Options.Alignment.HORIZONTAL_ALIGN);
		
		SequenceDocument document = (SequenceDocument) documents[0].getDocument();
			
		int maxSequenceLength = document.getSequenceLength();
		int start = selectionRange.getFirstSelectedResidue();
		int end = selectionRange.getLastSelectedResidue();

		child.addCustomOption(new SelectionGrabOption("selectionGrabber", new Interval(start, end)));
		child.getOption("selectionGrabber").addChangeListener(new SimpleListener(){
			public void objectChanged(){
				Interval interval = (Interval) child.getOption("selectionGrabber").getValue();
				IntegerOption start = (IntegerOption) child.getOption("selectionStart");
				start.setValue(interval.getMin());
				//TODO: Verify: Inclusive or Exclusive?
				IntegerOption end = (IntegerOption) child.getOption("selectionEnd");
				end.setValue(interval.getMaxExclusive());
			}
		});

		child.addIntegerOption("selectionStart","Region Start",start,0,maxSequenceLength);
		child.addIntegerOption("selectionEnd","Region End",end,0,2*maxSequenceLength);
		
		//reset to the current selection
		child.getOption("selectionStart").restoreDefault();
		child.getOption("selectionEnd").restoreDefault();
		
		JSpinner startComponent = (JSpinner) child.getOption("selectionStart").getComponent();
		startComponent.setValue(start);
		JSpinner endComponent = (JSpinner) child.getOption("selectionEnd").getComponent();
		endComponent.setValue(end);
		
		child.addFileSelectionOption("fileSelect", "Select a sequence to add", "");
		((FileSelectionOption) child.getOption("fileSelect")).setAllowMultipleSelection(true);
		((FileSelectionOption) child.getOption("fileSelect")).setSelectionType(JFileChooser.FILES_ONLY);
		
		child.addLabel("");
		child.addLabel("");
		child.addMultipleLineStringOption("filesLabel", "Selected Sequences:", "", files.size(), false);
		child.addLabel("");
		child.addButtonOption("clearButton","","Clear List");
		
		child.getOption("fileSelect").addChangeListener(new SimpleListener(){
			public void objectChanged(){
				String val = (String) child.getOption("fileSelect").getValue();
				val.replace("\"", "");
				String[] arr = val.split("; ");//may be multiple selections
				for (String s : arr){
					files.add(s.trim());
				}
				//update the list shown to the user
				StringBuilder sb = new StringBuilder();
				for (String s : files){
					sb.append(s);
					sb.append("\n");
				}
				child.getOption("filesLabel").setValueFromString(sb.toString());
			}
		});
		
		((ButtonOption) child.getOption("clearButton")).addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				files.clear();
				child.getOption("filesLabel").setValueFromString("");
			}
			
		});
		
		return child;
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
