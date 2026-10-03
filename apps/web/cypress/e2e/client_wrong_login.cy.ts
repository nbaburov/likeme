/* eslint-disable @typescript-eslint/no-unused-expressions */
describe("Client Wrong Login Flow", () => {
	beforeEach(() => {
		cy.visit("/");
	});
});

it("wrong_login", function () {
	cy.visit("/");

	cy.get(
		".fixed > .glass-nav > .flex > .hidden > .group > .relative"
	).click();
	cy.get(".mb-3 > :nth-child(1) > .mt-2 > .block").type("test");
	cy.get(".mb-6 > :nth-child(1) > .mt-2 > .block").type("test");

	cy.intercept("POST", "http://localhost:8080/api/auth/login", {
		statusCode: 500,
		body: {},
	}).as("loginRequest");

	cy.get("form > .bg-gradient-to-br").click();
	cy.wait("@loginRequest").its("response.statusCode").should("eq", 500);
});
