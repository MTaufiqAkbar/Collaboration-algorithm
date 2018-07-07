/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author MTaufiqAkbar
 */
public class DatabaseConnection {

    private final String url = "jdbc:postgresql://localhost:5432/dblipi";
    private final String user = "postgres";
    private final String password = "mtaufiqakbar";

    /**
     * Connect to the PostgreSQL database
     *
     * @return a Connection object
     */
    public Connection dbConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public static void main(String[] args) {
        DatabaseConnection db = new DatabaseConnection();
        //Untuk test connection
        db.OlahData();

    }

    public List<EntityPaper> getAllResearch() {

        ArrayList listPaper = new ArrayList();

        Connection connection;
        Statement statement;
        ResultSet resultSet;

        String query = "SELECT tath.string_agg AS author, \n"
                + "       taff.string_agg AS affiliation, \n"
                + "       tasub.strvalue AS subject,\n"
                + "       tath.displayorder, \n"
                + "       tath.parentdatasetfield_id, \n"
                + "       tath.datasetversion_id \n"
                + "FROM   (SELECT t1.string_agg, \n"
                + "               t1.parentdatasetfield_id, \n"
                + "               t1.displayorder, \n"
                + "               datasetfield.datasetversion_id \n"
                + "        FROM   (SELECT datasetfieldvalue.value AS string_agg, \n"
                + "                       datasetfieldcompoundvalue.parentdatasetfield_id AS parentdatasetfield_id, \n"
                + "                       datasetfield.datasetversion_id, \n"
                + "                       datasetfieldcompoundvalue.displayorder AS displayorder \n"
                + "                FROM   datasetfieldvalue, \n"
                + "                       datasetfield, \n"
                + "                       datasetfieldcompoundvalue \n"
                + "                GROUP  BY datasetfieldcompoundvalue.parentdatasetfield_id, \n"
                + "                          datasetfieldvalue.datasetfield_id = datasetfield.id, \n"
                + "                          datasetfield.parentdatasetfieldcompoundvalue_id = datasetfieldcompoundvalue.id, \n"
                + "                          datasetfield.datasetfieldtype_id = 9, \n"
                + "                          datasetfield.datasetversion_id, \n"
                + "                          datasetfieldvalue.value, \n"
                + "                          datasetfieldcompoundvalue.displayorder \n"
                + "                HAVING datasetfieldvalue.datasetfield_id = datasetfield.id \n"
                + "                       AND datasetfield.parentdatasetfieldcompoundvalue_id = datasetfieldcompoundvalue.id \n"
                + "                       AND datasetfieldcompoundvalue.parentdatasetfield_id IN ( \n"
                + "                           SELECT datasetfield.id \n"
                + "						   FROM datasetfield \n"
                + "                           WHERE datasetfield.datasetfieldtype_id = 8) \n"
                + "                       AND datasetfield.datasetfieldtype_id = 9 \n"
                + "                ORDER  BY datasetfieldcompoundvalue.parentdatasetfield_id) AS t1 \n"
                + "               LEFT OUTER JOIN datasetfield \n"
                + "                            ON ( t1.parentdatasetfield_id = datasetfield.id )\n"
                + "	) AS tath \n"
                + "\n"
                + "	-- LEFT JOIN dengan Subject\n"
                + "\n"
                + "LEFT JOIN (SELECT 	strvalue, \n"
                + "			datasetfield.datasetversion_id \n"
                + "            FROM   datasetfield_controlledvocabularyvalue \n"
                + "            LEFT JOIN datasetfield ON ( datasetfield_controlledvocabularyvalue.datasetfield_id = datasetfield.id ) \n"
                + "			LEFT JOIN controlledvocabularyvalue ON ( datasetfield_controlledvocabularyvalue.controlledvocabularyvalues_id = controlledvocabularyvalue.id ) \n"
                + "			WHERE  datasetfield.datasetfieldtype_id = 20 \n"
                + "			ORDER  BY datasetversion_id\n"
                + "		) AS tasub\n"
                + "		ON ( tath.datasetversion_id = tasub.datasetversion_id )  \n"
                + "	\n"
                + "       --LEFTJOIN DENGAN AFFILIATION \n"
                + "\n"
                + "LEFT JOIN  (SELECT  	t1.string_agg, \n"
                + "			t1.parentdatasetfield_id, \n"
                + "			t1.displayorder, \n"
                + "			datasetfield.datasetversion_id \n"
                + "        FROM   (SELECT datasetfieldvalue.value AS string_agg, \n"
                + "                       datasetfieldcompoundvalue.parentdatasetfield_id AS parentdatasetfield_id, \n"
                + "                       datasetfield.datasetversion_id, \n"
                + "                       datasetfieldcompoundvalue.displayorder AS displayorder \n"
                + "                FROM   datasetfieldvalue, \n"
                + "                       datasetfield, \n"
                + "                       datasetfieldcompoundvalue \n"
                + "                GROUP  BY datasetfieldcompoundvalue.parentdatasetfield_id, \n"
                + "                          datasetfieldvalue.datasetfield_id = datasetfield.id, \n"
                + "                          datasetfield.parentdatasetfieldcompoundvalue_id = datasetfieldcompoundvalue.id, \n"
                + "                          datasetfield.datasetfieldtype_id = 10, \n"
                + "                          datasetfield.datasetversion_id, \n"
                + "                          datasetfieldvalue.value, \n"
                + "                          datasetfieldcompoundvalue.displayorder \n"
                + "                HAVING datasetfieldvalue.datasetfield_id = datasetfield.id \n"
                + "                       AND datasetfield.parentdatasetfieldcompoundvalue_id = datasetfieldcompoundvalue.id \n"
                + "                       AND datasetfieldcompoundvalue.parentdatasetfield_id IN ( \n"
                + "                           SELECT datasetfield.id \n"
                + "						   FROM datasetfield \n"
                + "                           WHERE datasetfield.datasetfieldtype_id = 8) \n"
                + "                       AND datasetfield.datasetfieldtype_id = 10 \n"
                + "                ORDER  BY datasetfieldcompoundvalue.parentdatasetfield_id) AS t1 \n"
                + "               LEFT OUTER JOIN datasetfield \n"
                + "                            ON ( t1.parentdatasetfield_id = datasetfield.id )\n"
                + "	)  AS taff \n"
                + "    \n"
                + "	ON ( tath.parentdatasetfield_id = taff.parentdatasetfield_id AND tath.displayorder = taff.displayorder )  ";

        try {
            connection = dbConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);//"SELECT * FROM students;"
            while (resultSet.next()) {
                EntityPaper paper = new EntityPaper();
                paper.setAuthor(resultSet.getString("author"));
                paper.setAffiliation(resultSet.getString("affiliation"));
                paper.setSubject(resultSet.getString("subject"));
                paper.setDatasetversion_id(resultSet.getString("datasetversion_id"));
                listPaper.add(paper);
            }
            connection.close();
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listPaper;
    }

    void OlahData() {

        List<EntityPaper> entity = getAllResearch();
        ArrayList tempList = new ArrayList();
        ArrayList tempDataVersion = new ArrayList();
        ArrayList tempSortDataA = new ArrayList();
        ArrayList tempSortDataB = new ArrayList();
        ArrayList tempAuthorData = new ArrayList();
        for (int i = 0; i < entity.size(); i++) {

//            System.out.println(entity.get(i).getDatasetversion_id() + "  ||  " + entity.get(i).getAuthor() + "  ||  " + entity.get(i).getAffiliation() + "  ||  " + entity.get(i).getSubject());
            tempList.add(entity.get(i).getAuthor());
            tempDataVersion.add(entity.get(i).getDatasetversion_id());
            tempAuthorData.add(entity.get(i).getAuthor());

        }
        List<Integer> items = tempDataVersion;

        Map<Integer, Long> result = items.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("result-index.txt"), "utf-8"))) {
            writer.write(result.toString());
        } catch (Exception e) {

        }

        System.out.println(result);

////        for (int z = 0; z < entity.size(); z++) {
//        for (int p = 4; p < 5; p++) {
//            ArrayList<Integer> array = indexOfAll(tempAuthorData.get(p).toString(), tempList);
//
//            for (int a = 0; a < array.size(); a++) {
//                tempSortDataA.add(tempDataVersion.get(array.get(a)));
//            }
//
//        }
//
//        for (int p = 5; p < 6; p++) {
//            ArrayList<Integer> array = indexOfAll(tempAuthorData.get(p).toString(), tempList);
//
//            for (int a = 0; a < array.size(); a++) {
//                tempSortDataB.add(tempDataVersion.get(array.get(a)));
//            }
//
//        }
//
////        }^
//        for (int p = 0; p < tempSortDataA.size(); p++) {
//            tempAuthorData.get(p);
//        }
//        System.out.println(tempSortDataA);
//        System.out.println(tempSortDataB);
    }

    static ArrayList<Integer> indexOfAll(String obj, ArrayList list) {
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++) {
            if (obj.equals(list.get(i))) {
                indexList.add(i);
            }
        }
        return indexList;
    }

}
