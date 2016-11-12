package com.store.data.generator;

import com.store.data.generator.calculators.PriceCalculator;
import com.store.data.generator.calculators.StorageCostCalculator;
import com.store.data.generator.generators.EmployeeGenerator;
import com.store.data.generator.generators.ItemGenerator;
import com.store.data.generator.generators.NameGenerator;
import com.store.data.generator.models.*;
import com.store.data.generator.utils.NameRetriever;
import org.joda.time.DateTime;
import com.store.data.generator.generators.PurchaseGenerator;
import com.store.data.generator.utils.EmployeeSelector;
import com.store.data.generator.utils.ItemSelector;
import com.store.data.generator.utils.DateSelector;

import java.io.*;
import java.util.*;

/**
 * Purchases:
 * 1- Make sure number of items sold doesn't exceed current inventory
 * 2-
 *
 * Tracking stolen items
 *
 * Tracking how fast items sell
 *
 * Tracking Employee Performance
 *
 * Tracking Department Performance
 *
 * Comparing Department maintenance costs vs Department profits
 *
 * Tracking item sales to find which one are seasonal and when
 */

public class Demo
{
    public static void main(String args[]) throws Exception
    {
//        testEmployeeGenerator();
//        testItemsGenerator();
       testPurchaseGenerator();
//        writeToEmployeeList();
//       writeToItemList();
//        resetStocks();
    }

    private static void resetStocks() throws IOException
    {
        List<Item> itemList = new ArrayList<Item>();
        readItemFile(itemList);

        File ItFile = new File("ItemList.txt");

        PrintWriter writer = new PrintWriter(ItFile);
        writer.close();

        FileWriter ItWrite = new FileWriter(ItFile.getAbsoluteFile());
        BufferedWriter bWrite = new BufferedWriter(ItWrite);

        for(Item items: itemList)
        {
            String itString = items.getId() + " " + items.getItemType() + " " + items.getItemSize() + " " + items.getCostOfStoragePerUnit() + " " + items.getDepartment() + " " + 100 + " \n";
            bWrite.write(itString);
        }
        bWrite.close();
    }

    private static void testEmployeeGenerator() throws IOException
    {
        final NameRetriever nameRetriever = new NameRetriever();
        final NameGenerator nameGenerator = new NameGenerator(nameRetriever);
        final EmployeeGenerator employeeGenerator = new EmployeeGenerator(nameGenerator);
        List<Employee> employeeList = employeeGenerator.generateEmployees();

        for (Employee employee : employeeList)
        {
            System.out.println(employee);
        }

        System.out.println(employeeList.size());
    }

    private static void writeToEmployeeList() throws IOException
    {
        File empFile = new File("EmployeeList.txt");
        FileReader empRead = new FileReader(empFile.getAbsoluteFile());
        BufferedReader bRead = new BufferedReader(empRead);

        if(bRead.readLine() != null)
        {
            System.out.println("File is already written to, aborting");
            return;
        }

        final NameRetriever nameRetriever = new NameRetriever();
        final NameGenerator nameGenerator = new NameGenerator(nameRetriever);
        final EmployeeGenerator employeeGenerator = new EmployeeGenerator(nameGenerator);
        List<Employee> employeeList = employeeGenerator.generateEmployees();

        FileWriter empWrite = new FileWriter(empFile.getAbsoluteFile());
        BufferedWriter bWrite = new BufferedWriter(empWrite);

        for(Employee employee : employeeList)
        {
            String empString = employee.getId() + " " + employee.getFirstName() + " " + employee.getLastName() + " " + employee.getLevel() + " " + employee.getDepartment() + " \n";
            bWrite.write(empString);
        }
        bWrite.close();
    }

    private static void writeToItemList() throws IOException
    {
        File ItFile = new File("ItemList.txt");
        FileReader ItRead = new FileReader(ItFile.getAbsoluteFile());
        BufferedReader bRead = new BufferedReader(ItRead);

        if(bRead.readLine() != null)
        {
            System.out.println("File is already written to, aborting");
            return;
        }

        final StorageCostCalculator storageCostCalculator = new StorageCostCalculator();
        final ItemGenerator itemGenerator = new ItemGenerator(storageCostCalculator);
        List<Item> itemList = itemGenerator.listAllCombinations();

        FileWriter ItWrite = new FileWriter(ItFile.getAbsoluteFile());
        BufferedWriter bWrite = new BufferedWriter(ItWrite);

        for(Item items: itemList)
        {
            String itString = items.getId() + " " + items.getItemType() + " " + items.getItemSize() + " " + items.getCostOfStoragePerUnit() + " " + items.getDepartment() + " " + items.getStock() + " \n";
            bWrite.write(itString);
        }
        bWrite.close();
    }

    private static void testItemsGenerator() {
        final StorageCostCalculator storageCostCalculator = new StorageCostCalculator();
        final ItemGenerator itemGenerator = new ItemGenerator(storageCostCalculator);
        List<Item> itemList = itemGenerator.listAllCombinations();

        for (Item items : itemList) {
            System.out.println(items);
        }

        System.out.println(itemList.size());
    }

    private static void testPurchaseGenerator() throws IOException
    {
        List<Employee> employeeList = new ArrayList<>();
        readEmpFile(employeeList);

        List<Item> itemList = new ArrayList<Item>();
        readItemFile(itemList);

        int startYear, startMonth, startDay, endYear, endMonth, endDay;
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter starting date (Month Day Year)");
        startMonth = scan.nextInt();
        startDay = scan.nextInt();
        startYear = scan.nextInt();

        System.out.println("Enter ending date (Month Day Year) [does not generate purchases ON the end date] [keep start to end date range below ~5 months or else earliest dates will cut off on terminal]");

        endMonth = scan.nextInt();
        endDay = scan.nextInt();
        endYear = scan.nextInt();

        Random rand = new Random();
        DateTime start = new DateTime(startYear,startMonth,startDay,0,0,0);
        DateTime end = new DateTime(endYear, endMonth, endDay,0,0,0);

        final ItemSelector itemSel = new ItemSelector(itemList, rand);
        final EmployeeSelector empSel = new EmployeeSelector(employeeList, rand);

        final PriceCalculator priceCalculator = new PriceCalculator();
        final PurchaseGenerator purchaseGen = new PurchaseGenerator(start, end, empSel, itemSel, priceCalculator);
        List<Purchase> purchaseList = purchaseGen.generatePurchases(itemList, employeeList);

        File PurchFile = new File("PurchaseList.txt");
        FileWriter PurchWrite = new FileWriter(PurchFile, true);
        BufferedWriter bWrite = new BufferedWriter(PurchWrite);

        for(Purchase purchase : purchaseList)
        {
            String test = purchase.toString();
            bWrite.write(test);
        }
        bWrite.close();
    }

    private static List<Employee> readEmpFile(List<Employee> inputList) throws IOException
    {
        File empFile = new File("EmployeeList.txt");
        Scanner fileScan = new Scanner(empFile);

        if(!fileScan.hasNext())
        {
            System.out.println("File is empty, aborting");
            return null;
        }

        String val = null;

        while(fileScan.hasNext())
        {
            final Employee employee = new Employee(
                    Long.parseLong(fileScan.next(), 10),
                    fileScan.next(),
                    fileScan.next(),
                    Level.valueOf(fileScan.next()),
                    Department.valueOf(fileScan.next()));

            inputList.add(employee);
        }

        fileScan.close();

        return inputList;
    }

    private static List<Item> readItemFile(List<Item> inputList) throws IOException
    {
        File itFile = new File("ItemList.txt");
        Scanner fileScan = new Scanner(itFile);

        if(!fileScan.hasNext())
        {
            System.out.println("File is empty, aborting");
            return null;
        }

        String val = null;

        while(fileScan.hasNext())
        {
            final Item item = new Item(
                    Long.parseLong(fileScan.next(), 10),
                    ItemType.valueOf(fileScan.next()),
                    ItemSize.valueOf(fileScan.next()),
                    Double.parseDouble(fileScan.next()),
                    Department.valueOf(fileScan.next()),
                    Long.parseLong(fileScan.next(), 10));

            inputList.add(item);
        }

        fileScan.close();

        return inputList;
    }
}