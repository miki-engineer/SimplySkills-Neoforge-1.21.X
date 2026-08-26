package net.sweenus.simplyskills.util;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.minecraft.server.packs.PackType;
import net.sweenus.simplyskills.SimplySkills;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@SuppressWarnings({"resource", "ConstantConditions", "ResultOfMethodCallIgnored"})
public class FileCopier {
    private static final String DATA_PREFIX = PackType.SERVER_DATA.getDirectory() + '/';

    public static void copyFileToConfigDirectory() throws IOException {
        if (!ModList.get().isLoaded(SimplySkills.MOD_ID))
            return;

        //noinspection OptionalGetWithoutIsPresent
        Optional<Path> simplySkills$categoriesPath = Optional.of(ModList.get().getModFileById(SimplySkills.MOD_ID).getFile().findResource(
                DATA_PREFIX + SimplySkills.MOD_ID + '/' + "custom_trees" + '/' + "puffish_skills"
        ));

        if (simplySkills$categoriesPath.isEmpty())
            return;

        String configDirectory = FMLPaths.CONFIGDIR.get().toString() + "/" + "puffish_skills/"; // Config directory path

        Files.find(simplySkills$categoriesPath.get(), Integer.MAX_VALUE, ((path, basicFileAttributes) -> !basicFileAttributes.isRegularFile()), new FileVisitOption[0])
                .forEach(path -> {
                    File targetFile = new File(configDirectory, simplySkills$categoriesPath.get().relativize(path).toString());
                    if (targetFile.exists() && !FMLEnvironment.production)
                        targetFile.delete();
                    if (!targetFile.exists() || !FMLEnvironment.production) {
                        try {
                            targetFile.mkdirs();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        Files.find(simplySkills$categoriesPath.get(), Integer.MAX_VALUE, ((path, basicFileAttributes) -> basicFileAttributes.isRegularFile()), new FileVisitOption[0])
                    .forEach(path -> {
                        try (InputStream inputStream = Files.newInputStream(path)){
                            if (inputStream != null) {
                                File targetFile = new File(configDirectory, simplySkills$categoriesPath.get().relativize(path).toString());
                                //System.out.println(targetFile);
                                if (targetFile.exists() && !FMLEnvironment.production)
                                    targetFile.delete();
                                if (!targetFile.exists()) {
                                    if (targetFile.createNewFile())
                                        Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                }
                            } else {
                                System.out.println(inputStream + " is not a valid input");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
}
