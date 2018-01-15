/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.dev1.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.dev1.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.dev1.common.Url;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.record.Address;
import org.orcid.jaxb.model.v3.dev1.record.AffiliationType;
import org.orcid.jaxb.model.v3.dev1.record.Distinction;
import org.orcid.jaxb.model.v3.dev1.record.Education;
import org.orcid.jaxb.model.v3.dev1.record.Employment;
import org.orcid.jaxb.model.v3.dev1.record.ExternalID;
import org.orcid.jaxb.model.v3.dev1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.dev1.record.Funding;
import org.orcid.jaxb.model.v3.dev1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.dev1.record.Keyword;
import org.orcid.jaxb.model.v3.dev1.record.Membership;
import org.orcid.jaxb.model.v3.dev1.record.OtherName;
import org.orcid.jaxb.model.v3.dev1.record.PeerReview;
import org.orcid.jaxb.model.v3.dev1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.dev1.record.Qualification;
import org.orcid.jaxb.model.v3.dev1.record.Relationship;
import org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.dev1.record.Service;
import org.orcid.jaxb.model.v3.dev1.record.Work;
import org.orcid.jaxb.model.v3.dev1.record.WorkBulk;
import org.orcid.jaxb.model.v3.dev1.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.dev1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.dev1.record.summary.Employments;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_EmploymentsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegatorV3_0_dev1")
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword> serviceDelegator;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployments(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment(ORCID, 17L);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentSummaryWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmploymentSummary(ORCID, 17L);
    }

    @Test
    public void testViewEmploymentsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmployments(ORCID);
        Employments element = (Employments) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employments", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewEmploymentReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmployment(ORCID, 17L);
        Employment element = (Employment) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employment/17", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewEmploymentSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmploymentSummary(ORCID, 17L);
        EmploymentSummary element = (EmploymentSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employment/17", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        Utils.verifyLastModified(employment.getLastModifiedDate());
        assertEquals(Long.valueOf(5L), employment.getPutCode());
        assertEquals("/4444-4444-4444-4446/employment/5", employment.getPath());
        assertEquals("Employment Dept # 1", employment.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), employment.getVisibility().value());
    }

    @Test
    public void testViewLimitedEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 11L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        Utils.verifyLastModified(employment.getLastModifiedDate());
        assertEquals(Long.valueOf(11L), employment.getPutCode());
        assertEquals("/4444-4444-4444-4446/employment/11", employment.getPath());
        assertEquals("Employment Dept # 4", employment.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), employment.getVisibility().value());
    }

    @Test
    public void testViewPrivateEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        Utils.verifyLastModified(employment.getLastModifiedDate());
        assertEquals(Long.valueOf(5L), employment.getPutCode());
        assertEquals("/4444-4444-4444-4446/employment/5", employment.getPath());
        assertEquals("Employment Dept # 1", employment.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), employment.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateEmploymentWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment("4444-4444-4444-4446", 10L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewEmploymentThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment("4444-4444-4444-4446", 4L);
        fail();
    }

    @Test
    public void testViewEmployments() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewEmployments(ORCID);
        assertNotNull(r);
        Employments employments = (Employments) r.getEntity();
        assertNotNull(employments);
        assertEquals("/0000-0000-0000-0003/employments", employments.getPath());
        Utils.verifyLastModified(employments.getLastModifiedDate());
        assertNotNull(employments.getSummaries());
        assertEquals(4, employments.getSummaries().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for (EmploymentSummary summary : employments.getSummaries()) {
            Utils.verifyLastModified(summary.getLastModifiedDate());
            if (Long.valueOf(17).equals(summary.getPutCode())) {
                assertEquals("PUBLIC Department", summary.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(18).equals(summary.getPutCode())) {
                assertEquals("LIMITED Department", summary.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(19).equals(summary.getPutCode())) {
                assertEquals("PRIVATE Department", summary.getDepartmentName());
                found3 = true;
            } else if (Long.valueOf(23).equals(summary.getPutCode())) {
                assertEquals("SELF LIMITED Department", summary.getDepartmentName());
                found4 = true;
            } else {
                fail("Invalid education found: " + summary.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
    }

    @Test
    public void testReadPublicScope_Employments() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(r);
        assertEquals(Employment.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewEmploymentSummary(ORCID, 17L);
        assertNotNull(r);
        assertEquals(EmploymentSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewEmployment(ORCID, 18L);
        serviceDelegator.viewEmploymentSummary(ORCID, 18L);
        // Limited that am not the source of should fail
        try {
            serviceDelegator.viewEmployment(ORCID, 23L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEmploymentSummary(ORCID, 23L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private that am the source of should work
        serviceDelegator.viewEmployment(ORCID, 19L);
        serviceDelegator.viewEmploymentSummary(ORCID, 19L);
        // Private that am not the source of should fail
        try {
            serviceDelegator.viewEmployment(ORCID, 24L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEmploymentSummary(ORCID, 24L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4447");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getSummaries());
        assertNotNull(summary.getEmployments().getSummaries().get(0));
        assertEquals(Long.valueOf(13), summary.getEmployments().getSummaries().get(0).getPutCode());

        response = serviceDelegator.createEmployment("4444-4444-4444-4447", (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewActivities("4444-4444-4444-4447");
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getEmployments());
        Utils.verifyLastModified(summary.getEmployments().getLastModifiedDate());
        assertNotNull(summary.getEmployments().getSummaries());

        boolean haveOld = false;
        boolean haveNew = false;

        for (EmploymentSummary eSummary : summary.getEmployments().getSummaries()) {
            assertNotNull(eSummary.getPutCode());
            Utils.verifyLastModified(eSummary.getLastModifiedDate());
            if (eSummary.getPutCode() == 13L) {
                assertEquals("Employment Dept # 1", eSummary.getDepartmentName());
                haveOld = true;
            } else {
                assertEquals(putCode, eSummary.getPutCode());
                assertEquals("My department name", eSummary.getDepartmentName());
                haveNew = true;
            }
        }

        assertTrue(haveOld);
        assertTrue(haveNew);
    }

    @Test(expected = OrcidValidationException.class)
    public void testAddEmploymentNoStartDate() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Employment employment = (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT);
        employment.setStartDate(null);
        serviceDelegator.createEmployment("4444-4444-4444-4447", employment);
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddEmploymentsDuplicateExternalIDs() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);

        ExternalID e1 = new ExternalID();
        e1.setRelationship(Relationship.SELF);
        e1.setType("erm");
        e1.setUrl(new Url("https://orcid.org"));
        e1.setValue("err");

        ExternalID e2 = new ExternalID();
        e2.setRelationship(Relationship.SELF);
        e2.setType("err");
        e2.setUrl(new Url("http://bbc.co.uk"));
        e2.setValue("erm");

        ExternalIDs externalIDs = new ExternalIDs();
        externalIDs.getExternalIdentifier().add(e1);
        externalIDs.getExternalIdentifier().add(e2);

        Employment employment = (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT);
        employment.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createEmployment("4444-4444-4444-4447", employment);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());

        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        try {
            Employment duplicate = (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT);
            duplicate.setExternalIDs(externalIDs);
            serviceDelegator.createEmployment("4444-4444-4444-4447", duplicate);
        } finally {
            serviceDelegator.deleteAffiliation("4444-4444-4444-4447", putCode);
        }
    }

    @Test
    public void testUpdateEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals("Employment Dept # 1", employment.getDepartmentName());
        assertEquals("Researcher", employment.getRoleTitle());
        Utils.verifyLastModified(employment.getLastModifiedDate());
        LastModifiedDate before = employment.getLastModifiedDate();

        employment.setDepartmentName("Updated department name");
        employment.setRoleTitle("The updated role title");

        // disambiguated org is required in API v3
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        employment.getOrganization().setDisambiguatedOrganization(disambiguatedOrg);

        response = serviceDelegator.updateEmployment("4444-4444-4444-4446", 5L, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        employment = (Employment) response.getEntity();
        assertNotNull(employment);
        Utils.verifyLastModified(employment.getLastModifiedDate());
        assertTrue(employment.getLastModifiedDate().after(before));
        assertEquals("Updated department name", employment.getDepartmentName());
        assertEquals("The updated role title", employment.getRoleTitle());

        // Rollback changes
        employment.setDepartmentName("Employment Dept # 1");
        employment.setRoleTitle("Researcher");

        response = serviceDelegator.updateEmployment("4444-4444-4444-4446", 5L, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateEmploymentDuplicateExternalIDs() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);

        ExternalID e1 = new ExternalID();
        e1.setRelationship(Relationship.SELF);
        e1.setType("erm");
        e1.setUrl(new Url("https://orcid.org"));
        e1.setValue("err");

        ExternalID e2 = new ExternalID();
        e2.setRelationship(Relationship.SELF);
        e2.setType("err");
        e2.setUrl(new Url("http://bbc.co.uk"));
        e2.setValue("erm");

        ExternalIDs externalIDs = new ExternalIDs();
        externalIDs.getExternalIdentifier().add(e1);
        externalIDs.getExternalIdentifier().add(e2);

        Employment employment = (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT);
        employment.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createEmployment("4444-4444-4444-4447", employment);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode1 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        Employment another = (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT);
        response = serviceDelegator.createEmployment("4444-4444-4444-4447", another);
        
        map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        resultWithPutCode = (List<?>) map.get("Location");
        Long putCode2 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
        
        response = serviceDelegator.viewEmployment("4444-4444-4444-4447", putCode2);
        another = (Employment) response.getEntity();
        another.setExternalIDs(externalIDs);
        
        try {
            serviceDelegator.updateEmployment("4444-4444-4444-4447", putCode2, another);
        } finally {
            serviceDelegator.deleteAffiliation("4444-4444-4444-4447", putCode1);
            serviceDelegator.deleteAffiliation("4444-4444-4444-4447", putCode2);
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateEmploymentYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 11L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        employment.setDepartmentName("Updated department name");
        employment.setRoleTitle("The updated role title");
        serviceDelegator.updateEmployment("4444-4444-4444-4446", 11L, employment);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateEmploymentChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Visibility.PRIVATE, employment.getVisibility());

        employment.setVisibility(Visibility.LIMITED);

        response = serviceDelegator.updateEmployment("4444-4444-4444-4446", 5L, employment);
        fail();
    }

    @Test
    public void testUpdateEmploymentLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Visibility.PRIVATE, employment.getVisibility());

        employment.setVisibility(null);

        response = serviceDelegator.updateEmployment("4444-4444-4444-4446", 5L, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Visibility.PRIVATE, employment.getVisibility());
    }

    @Test(expected = NoResultException.class)
    public void testDeleteEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4444", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4444", 14L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);

        response = serviceDelegator.deleteAffiliation("4444-4444-4444-4444", 14L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        serviceDelegator.viewEmployment("4444-4444-4444-4444", 14L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteEmploymentYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteAffiliation("4444-4444-4444-4446", 11L);
        fail();
    }
}
