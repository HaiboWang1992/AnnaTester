package util;

import analysis.AnnotationWrapper;
import org.apache.commons.io.FileUtils;
import analysis.TypeWrapper;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static util.Utility.ANNOTATION_LIBRARY_PATH;
import static util.Utility.getDirectFilenamesFromFolder;
import static util.Utility.getFilenamesFromFolder;
import static util.Utility.sep;

public class AnnotationMeasurement {

    public static void MeasureAnnotatedElements() {
        final String seed_folder_path = "/Users/austin/projects/Statfier/seeds";
        List<String> paths = Utility.getFilenamesFromFolder(seed_folder_path, true);
        List<TypeWrapper> wrappers = new ArrayList<>();
        int cntAnnotatedTypes = 0, cntAnnotatedMethods = 0, cntAnnotatedFields = 0;
        int cntTypes = 0, cntMethods = 0, cntFields = 0;
        Set<String> appearedAnnotations = new HashSet<>();
        Set<String> reducedAnnotations = new HashSet<>();
        for(int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            TypeWrapper wrapper = new TypeWrapper(path);
            wrappers.add(wrapper);
            List<TypeDeclaration> types = wrapper.getTypes();
            for(TypeDeclaration type : types) {
                cntTypes++;
                for(ASTNode node : (List<ASTNode>) type.modifiers()) {
                    if(node.toString().contains("ExpectWarning")
                            || node.toString().contains("NoWarning")
                            || node.toString().contains("DesireWarning")
                            || node.toString().contains("DesireNoWarning")) {
                        continue;
                    }
                    if(node instanceof SingleMemberAnnotation || node instanceof MarkerAnnotation || node instanceof NormalAnnotation) {
                        cntAnnotatedTypes++;
                        appearedAnnotations.add(node.toString());
                        break;
                    }
                }
                for(FieldDeclaration field : type.getFields()) {
                    cntFields++;
                    for(ASTNode node : (List<ASTNode>) field.modifiers()) {
                        if(node.toString().contains("ExpectWarning")
                                || node.toString().contains("NoWarning")
                                || node.toString().contains("DesireWarning")
                                || node.toString().contains("DesireNoWarning")) {
                            continue;
                        }
                        if(node instanceof SingleMemberAnnotation || node instanceof MarkerAnnotation || node instanceof NormalAnnotation) {
                            cntAnnotatedFields++;
                            appearedAnnotations.add(node.toString());
                            break;
                        }
                    }
                }
                for(MethodDeclaration method : type.getMethods()) {
                    cntMethods++;
                    for(ASTNode node : (List<ASTNode>) method.modifiers()) {
                        if(node.toString().contains("ExpectWarning")
                                || node.toString().contains("NoWarning")
                                || node.toString().contains("DesireWarning")
                                || node.toString().contains("DesireNoWarning")
                                || node.toString().contains("Override")) {
                            continue;
                        }
                        if(node instanceof SingleMemberAnnotation || node instanceof MarkerAnnotation || node instanceof NormalAnnotation) {
                            cntAnnotatedMethods++;
                            appearedAnnotations.add(node.toString());
                            break;
                        }
                    }
                }
            }
        }
        for(String annotation : appearedAnnotations) {
            String temp = annotation.strip();
            if(annotation.contains("(")) {
                temp = annotation.substring(0, annotation.indexOf("("));
            }
            if(temp.contains(".")) {
                temp = temp.substring(temp.lastIndexOf(".") + 1);
                temp = "@" + temp;
            }
            reducedAnnotations.add(temp);
        }
        for(String reducedAnnotation : reducedAnnotations) {
            System.out.println(reducedAnnotation);
        }
        System.out.println("Cnt of appeared annotations: " + reducedAnnotations.size());
        System.out.println("Types: " + cntAnnotatedTypes + " " + cntTypes + " Ratio: " + (double)(cntAnnotatedTypes) / (cntTypes));
        System.out.println("Methods: " + cntAnnotatedMethods + " " + cntMethods + " Ratio: " + (double)(cntAnnotatedMethods) / (cntMethods));
        System.out.println("Fields: " + cntAnnotatedFields + " " + cntFields + " Ratio: " + (double)(cntAnnotatedFields) / cntFields);
    }

    public static void move() {
        List<String> folders = getDirectFilenamesFromFolder(ANNOTATION_LIBRARY_PATH, true);
        for(String folder : folders) {
            List<String> paths = getFilenamesFromFolder(folder, true);
            for(int i = 0; i < paths.size(); i++) {
                File file = new File(paths.get(i));
                String targetPath = folder + sep + file.getName();
                File targetFile = new File(targetPath);
                if(!targetFile.exists()) {
                    try {
                        FileUtils.copyFile(file, targetFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void MeasureAnnotations() {
        List<String> filePaths = getFilenamesFromFolder(ANNOTATION_LIBRARY_PATH, true);
        System.out.println("Size: " + filePaths.size());
        List<ArrayList<String>> paths = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            paths.add(new ArrayList<>());
        }
        int[] cnt = new int[3];
        List<String> invalidPaths = new ArrayList<>();
        Set<String> deletePaths = new HashSet<>();
        Set<String> sourceAnnotations = new HashSet<>();
        for(String filePath : filePaths) {
            List<AnnotationWrapper> wrappers = AnnotationWrapper.annotationParser(filePath);
            System.out.println(filePath);
            if(wrappers.size() <= 0) {
                invalidPaths.add(filePath);
            }
            for(AnnotationWrapper wrapper : wrappers) {
                if(wrapper.getRetention() != 0) {
                    deletePaths.add(wrapper.getFilePath());
                } else {
                    sourceAnnotations.add(filePath);
                }
                cnt[wrapper.getRetention()]++;
                paths.get(wrapper.getRetention()).add(wrapper.getFilePath());
            }
        }
        deletePaths.addAll(invalidPaths);
        for(String sourcePath : sourceAnnotations) {
            if(deletePaths.contains(sourcePath)) {
                deletePaths.remove(sourcePath);
            }
        }
        try {
            for (String filePath : deletePaths) {
                FileUtils.delete(new File(filePath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MeasureAnnotations();
    }

}
