import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.impl.method.*;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by remiprevost on 13/01/2016.
 */
public class Main {

    public static void main(String[] args) throws URISyntaxException, AlignmentException, FileNotFoundException, UnsupportedEncodingException {
        URI onto1 = new URI("http://oaei.ontologymatching.org/tests/101/onto.rdf");
        URI onto2 = new URI("http://oaei.ontologymatching.org/tests/304/onto.rdf");
        List alignment_list = new ArrayList<AlignmentProcess>();
        alignment_list.add(new NameEqAlignment());
        alignment_list.add(new EditDistNameAlignment());
        alignment_list.add(new SMOANameAlignment());
        alignment_list.add(new NameAndPropertyAlignment());
        alignment_list.add(new ClassStructAlignment());

        for (Object alignment : alignment_list) {
            ((AlignmentProcess)alignment).init(onto1, onto2);
            ((AlignmentProcess)alignment).align(null, new Properties());
            System.out.println("Num corresp . générées : " + ((AlignmentProcess)alignment).nbCells());
            render(((AlignmentProcess)alignment),((AlignmentProcess)alignment).getClass().toString());
            evaluate((AlignmentProcess)alignment);
        }
    }

    public static void render(Alignment alignment, String match_name) throws FileNotFoundException, UnsupportedEncodingException, AlignmentException {
        PrintWriter writer;
        FileOutputStream f = new FileOutputStream(new File("output"+match_name+".rdf"));
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(f, "UTF-8")), true);
        AlignmentVisitor renderer = new RDFRendererVisitor(writer);
        alignment.render(renderer);
        writer.flush();
        writer.close();
    }

    public static void evaluate(Alignment alignment) throws URISyntaxException, AlignmentException {
        URI reference = new URI("http://oaei.ontologymatching.org/tests/304/refalign.rdf");
        AlignmentParser aparser = new AlignmentParser(0);
        Alignment refalign = aparser.parse(reference);
        PRecEvaluator evaluator = new PRecEvaluator(refalign, alignment);
        evaluator.eval(new Properties());
        System.out.println(alignment.getClass().toString());
        System.out.println("Precision : " + evaluator.getPrecision());
        System.out.println("Recall :" + evaluator.getRecall());
        System.out.println("FMeasure :" + evaluator.getFmeasure());
        System.out.println();
    }
}
