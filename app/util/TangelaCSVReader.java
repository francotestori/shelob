package util;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.util.Map;

/**
 * Created by franco on 05/06/2015.
 */
public class TangelaCSVReader {

    private static final String CSV_FILENAME = "C:\\users-argentina.csv";

    private static CellProcessor[] getProcessors() {
        final CellProcessor[] processors = new CellProcessor[]{
                new UniqueHashCode(),//tangela_id
                new NotNull(),//name
                new Optional(),//bio
                new Optional(),//role
                new Optional(new ParseInt()),//follower_count
                new Optional(),//angelList_url
                new Optional(),//image
                new Optional(),//blog_url
                new Optional(),//bio_url
                new Optional(),//twitter_url
                new Optional(),//facebook_url
                new Optional(),//linkedin_url
                new Optional(),//what_ive_built
                new Optional(),//what_i_do
                new Optional(),//investor
        };

        return processors;
    }

    private static void readWithCsvMapReader() throws Exception {

        ICsvMapReader mapReader = null;
        try {
            mapReader = new CsvMapReader(new FileReader(CSV_FILENAME), CsvPreference.STANDARD_PREFERENCE);

            // the header columns are used as the keys to the Map
            final String[] header = mapReader.getHeader(true);
            final CellProcessor[] processors = getProcessors();

            Map<String, Object> customerMap;
            while( (customerMap = mapReader.read(header, processors)) != null ) {
                System.out.println(String.format("lineNo=%s, rowNo=%s, customerMap=%s", mapReader.getLineNumber(),
                        mapReader.getRowNumber(), customerMap));
            }

        }
        finally {
            if( mapReader != null ) {
                mapReader.close();
            }
        }
    }

    public static void main(String[] args) {
        try {
            readWithCsvMapReader();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
