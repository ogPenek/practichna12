import java.io.*;
import java.util.Scanner;

public class TextEditor {
    private static final String FILE_NAME = "textfile.txt";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        
        while (running) {
            printMenu();
            int choice = getIntInput("Виберіть опцію: ");
            
            switch (choice) {
                case 1:
                    writeToFile();
                    break;
                case 2:
                    readFromFile();
                    break;
                case 3:
                    appendMultipleLines();
                    break;
                case 4:
                    readRangeFromFile();
                    break;
                case 5:
                    insertAtLine();
                    break;
                case 6:
                    running = false;
                    System.out.println("Вихід з редактора.");
                    break;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
        
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\nМеню текстового редактора:");
        System.out.println("1. Записати рядок у файл (перезаписати)");
        System.out.println("2. Прочитати весь вміст файлу");
        System.out.println("3. Додати декілька рядків у файл");
        System.out.println("4. Прочитати діапазон рядків з файлу");
        System.out.println("5. Вставити рядок у певну позицію");
        System.out.println("6. Вийти з редактора");
    }

    private static void writeToFile() {
        System.out.print("Введіть рядок для запису: ");
        String line = scanner.nextLine();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("1. " + line);
            System.out.println("Рядок успішно записано.");
        } catch (IOException e) {
            System.out.println("Помилка при записі у файл: " + e.getMessage());
        }
    }

    private static void readFromFile() {
        System.out.println("\nВміст файлу:");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            int lineNumber = 1;
            
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                lineNumber++;
            }
            
            if (lineNumber == 1) {
                System.out.println("Файл порожній.");
            }
        } catch (IOException e) {
            System.out.println("Помилка при читанні файлу: " + e.getMessage());
        }
    }

    private static void appendMultipleLines() {
        System.out.println("Введіть рядки для додавання (введіть 'exit' для завершення):");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            int lineCount = countLines();
            
            while (true) {
                System.out.print((lineCount + 1) + ". ");
                String line = scanner.nextLine();
                
                if (line.equalsIgnoreCase("exit")) {
                    break;
                }
                
                writer.newLine();
                writer.write((lineCount + 1) + ". " + line);
                lineCount++;
            }
            
            System.out.println("Рядки успішно додані.");
        } catch (IOException e) {
            System.out.println("Помилка при додаванні рядків: " + e.getMessage());
        }
    }

    private static void readRangeFromFile() {
        int startLine = getIntInput("Введіть початковий номер рядка: ");
        int endLine = getIntInput("Введіть кінцевий номер рядка: ");
        
        if (startLine > endLine) {
            System.out.println("Початковий номер повинен бути менше або рівний кінцевому.");
            return;
        }
        
        System.out.println("\nВміст файлу з рядка " + startLine + " по " + endLine + ":");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            int currentLine = 1;
            
            while ((line = reader.readLine()) != null && currentLine <= endLine) {
                if (currentLine >= startLine) {
                    System.out.println(line);
                }
                currentLine++;
            }
            
            if (currentLine <= startLine) {
                System.out.println("Файл містить менше рядків ніж вказаний діапазон.");
            }
        } catch (IOException e) {
            System.out.println("Помилка при читанні файлу: " + e.getMessage());
        }
    }

    private static void insertAtLine() {
        int targetLine = getIntInput("Введіть номер рядка для вставки: ");
        
        if (targetLine < 1) {
            System.out.println("Номер рядка повинен бути більше 0.");
            return;
        }
        
        System.out.print("Введіть текст для вставки: ");
        String newLine = scanner.nextLine();
        
        try {
            String[] lines = readAllLines();
            
            if (targetLine > lines.length + 1) {
                System.out.println("Невірний номер рядка. Максимально можливий номер: " + (lines.length + 1));
                return;
            }
            
            String[] newLines = new String[lines.length + 1];
            
            System.arraycopy(lines, 0, newLines, 0, targetLine - 1);
            
            newLines[targetLine - 1] = targetLine + ". " + newLine;
            
            for (int i = targetLine; i < newLines.length; i++) {
                String oldLine = lines[i - 1];
                int oldLineNumber = Integer.parseInt(oldLine.substring(0, oldLine.indexOf('.')));
                newLines[i] = (oldLineNumber + 1) + oldLine.substring(oldLine.indexOf('.'));
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (int i = 0; i < newLines.length; i++) {
                    writer.write(newLines[i]);
                    if (i < newLines.length - 1) {
                        writer.newLine();
                    }
                }
            }
            
            System.out.println("Рядок успішно вставлено.");
        } catch (IOException e) {
            System.out.println("Помилка при роботі з файлом: " + e.getMessage());
        }
    }

    private static String[] readAllLines() throws IOException {
        String[] lines = new String[countLines()];
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            for (int i = 0; i < lines.length; i++) {
                lines[i] = reader.readLine();
            }
        }
        
        return lines;
    }

    private static int countLines() throws IOException {
        int lines = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            while (reader.readLine() != null) {
                lines++;
            }
        }
        
        return lines;
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Будь ласка, введіть ціле число.");
            }
        }
    }
}