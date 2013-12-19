package org.zanata.git

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Repository
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*



/**
 *
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
class JGitTest {

    private String localPath, remotePath;
    private Repository localRepo;
    private Git git;

    @BeforeClass
    public void init() throws IOException {
        localPath = "./git/testrepo";
        remotePath = "git@github.com:me/mytestrepo.git";
        localRepo = new FileRepository(localPath + "/.git");
        git = new Git(localRepo);
    }

    @AfterClass
    public void destroyRepo() throws IOException {
        FileUtils.deleteDirectory(new File(localPath));
    }

    @Test(priority = 1)
    public void testCreate() throws IOException {
        Repository newRepo = new FileRepository(localPath + "/.git");
        newRepo.create();
    }

    /*
     * @Test public void testClone() throws IOException, NoFilepatternException
     * { Git.cloneRepository() .setURI(remotePath) .setDirectory(new
     * File(localPath)) .call(); }
     */

    @Test(priority = 2)
    public void testAdd() throws Exception {
        File myfile = new File(localPath + "/myfile");
        myfile.createNewFile();
        git.add().addFilepattern("myfile").call();
    }

    @Test(priority = 2)
    public void testAddJsonObj() throws Exception {
        def builder = new JsonBuilder()
        def storedObject =
            [name: "Object name", description: "Description",
                        "creationDate": new Date()]
        builder.setContent(storedObject)

        File myfile = new File(localPath + "/jsonfile")
        myfile << builder.toPrettyString()
        git.add().addFilepattern("jsonfile").call();
    }

    @Test(priority = 3)
    public void testCommit() throws Exception {
        git.commit().setMessage("Added myfile").call();
    }

    /*@Test
    public void testPush() throws Exception {
        git.push().call();
    }*/

    @Test(priority = 4)
    public void testReadObject() {
        def slurper = new JsonSlurper()
        File myfile = new File(localPath + "/jsonfile")
        def json = myfile.text
        def storedObject = slurper.parseText(json)

        assertThat(storedObject.name, is("Object name"))
    }

    @Test(priority = 5)
    public void createMaster() throws Exception {
        git.branchCreate().setName("master").setForce(true).call();
    }

    /*@Test
    public void testPull() throws Exception {
        git.pull().call();
    }*/
}
