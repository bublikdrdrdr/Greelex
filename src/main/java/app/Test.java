package app;

import app.generator.MappingGenerator;
import app.preprocessor.MappingFileBuilder;

public class Test {

    public static void main(String... args) {
        String exapleScript = ".accessMode=lombok,\n" +
                ".relationFetchType=eager,\n" +
                ".collectionFetchType=lazy,.tableNaming=UNDERSCORE,\n" +
                ".columnNaming=UNDERSCORE,\n" +
                "AbstractEntity(super) {\n" +
                "\tid: long primary\n" +
                "},\n" +
                "User(AbstractEntity) {\t\n" +
                "\tusername: string unique,\n" +
                "\tname: string null,\n" +
                "\ttype: UserType eager,\n" +
                "\tprofile: Profile(user) eager join,\n" +
                "\tregisteredBy: User null lazy,\n" +
                "\tpictures: Picture[](owner) lazy,\n" +
                "\tprofilePictures: \"profile_pictures\" Picture[set](owner) lazy\n" +
                "},\n" +
                "//comment\t\n" +
                "UserType: \"user_types\" {\n" +
                "\tid: long primary,\n" +
                "\tname: string unique,\n" +
                "\tusers: User[] lazy\n" +
                "},\n" +
                "Picture(AbstractEntity): \"user_pictures\" {\n" +
                "\timage: blob(5m),\n" +
                "\towner: User lazy\n" +
                "},\n" +
                "Profile(AbstractEntity) {\n" +
                "\tvisible: bool,\n" +
                "\tuser: User eager\n" +
                "}";
        char[] charArray = exapleScript.toCharArray();

        MappingGenerator mappingGenerator = new MappingGenerator("app.asd");
        mappingGenerator.generate(MappingFileBuilder.INSTANCE.parse(charArray)).forEach(generatedFile -> {
            System.out.println("=========== " + generatedFile.getName());
            System.out.println("============================");
            System.out.println(generatedFile.getContent());
            System.out.println("============================");
        });
    }
}