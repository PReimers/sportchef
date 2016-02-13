/**
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2015 Marcus Fihlon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/ <http://www.gnu.org/licenses/>>.
 */
package ch.sportchef.business.user.bundary;

import ch.sportchef.test.IntegrationTests;
import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.junit.Rule;
import org.junit.experimental.categories.Category;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Category(IntegrationTests.class)
public class UserResourceIT {

    @Rule
    public final JAXRSClientProvider provider = buildWithURI("http://localhost:8080/sportchef/api/users");

    private long getUserId(final String location) {
        return Long.parseLong(location.substring(location.lastIndexOf("/") + 1));
    }

    private void readOneUserWithSuccess(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location)
                .request(MediaType.APPLICATION_JSON).get();
        final JsonObject jsonObject = response.readEntity(JsonObject.class);

        // assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertNotNull(jsonObject);
        assertThat(jsonObject.getJsonNumber("userId").longValue(), is(getUserId(location)));
        assertThat(jsonObject.getString("firstName"), is("John"));
        assertThat(jsonObject.getString("lastName"), is("Doe"));
        assertThat(jsonObject.getString("phone"), is("+41 79 555 00 01"));
        assertThat(jsonObject.getString("email"), is("john.doe@sportchef.ch"));
    }

    private void readOneUserWithNotFound(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location)
                .request(MediaType.APPLICATION_JSON).get();
        final JsonObject jsonObject = response.readEntity(JsonObject.class);

        // assert
        assertThat(response.getStatus(), is(NOT_FOUND.getStatusCode()));
        assertNull(jsonObject);
    }

    private JsonObject updateUserWithSuccess(final String location) {
        // arrange
        final JsonObject userToUpdate = Json.createObjectBuilder()
                .add("userId", getUserId(location))
                .add("firstName", "Jane")
                .add("lastName", "Doe")
                .add("phone", "+41 79 555 00 01")
                .add("email", "jane.doe@sportchef.ch")
                .build();

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).put(Entity.json(userToUpdate));
        final JsonObject jsonObject = response.readEntity(JsonObject.class);

        //assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(response.getHeaderString("Location"), is(location));
        assertNotNull(jsonObject);
        assertThat(jsonObject.getJsonNumber("userId").longValue(), is(getUserId(location)));
        assertThat(jsonObject.getString("firstName"), is("Jane"));
        assertThat(jsonObject.getString("lastName"), is("Doe"));
        assertThat(jsonObject.getString("phone"), is("+41 79 555 00 01"));
        assertThat(jsonObject.getString("email"), is("jane.doe@sportchef.ch"));

        return userToUpdate;
    }

    private void updateUserWithConflict(final String location, final JsonObject userToUpdate) {
        // arrange

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).put(Entity.json(userToUpdate));

        //assert
        assertThat(response.getStatus(), is(CONFLICT.getStatusCode()));
    }

    private void updateUserWithNotFound(final String location) {
        // arrange
        final JsonObject userToUpdate = Json.createObjectBuilder()
                .add("userId", getUserId(location))
                .add("firstName", "Jane")
                .add("lastName", "Doe")
                .add("phone", "+41 79 555 00 01")
                .add("email", "jane.doe@sportchef.ch")
                .build();

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).put(Entity.json(userToUpdate));

        //assert
        assertThat(response.getStatus(), is(NOT_FOUND.getStatusCode()));
    }

    private void deleteUserWithSuccess(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).delete();

        //assert
        assertThat(response.getStatus(), is(NO_CONTENT.getStatusCode()));
    }

    private void deleteUserWithNotFound(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).delete();

        //assert
        assertThat(response.getStatus(), is(NOT_FOUND.getStatusCode()));
    }

}
