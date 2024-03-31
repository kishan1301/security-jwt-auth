# security-jwt-auth

To run the application, please execute "docker-compose up".


# API Testing Process

1. Import "auth-APIs.postman_collection.json" provided in the project.
2. All related APIs have been labeled like user-signup, user-sing-in, refresh-token, etc.

<!-- 

# User signup
	API requests:
	# Execute the API by entering email and password in the basic auth section.
	# Or Authorization header can be included explicitly having values as Basic Base64Encoded(email:password)
	* Note: firstName and lastName are optional.

	API response:
	Response will have a user-details object having email, firstname, lastname, etc.


# User login
To login, please provide a Authorization header having values as Basic Base64Encoded(email:password).
Or use postman and enter values from Authorization tab.
Please hit enter after providing values.
 -->

 # Swagger
 Open url http://localhost:8080/swagger-ui/index.html#/ to know details of all the APIs.
 Manually include Authorization token in cURL requests obtained from swagger-ui.


 # API requirements.

 APIs that requires Basic Auth, email-password based authentication.
 	- user-signup
 	- user-signin

 APIs that requires Bearer Auth, jwt based authentication.
 	- get-users
 	- invalidate-token
 	- token-refresh-jwt

 