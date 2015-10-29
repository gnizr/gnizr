This page describes the specification of gnizr XML configuration file. This configuration controls the JDBC connectivity to gnizr MySQL database and the runtime behavior of a gnizr deployment.

## Edit gnizr-config.xml ##

The gnizr web application comes with a configuration file called `gnizr-config.xml`. This file is located in the directory `gnizr/WEB-INF/classes`.

You should edit this configuration file to suit your machine's configuration. Configurations are defined as a set of property/value pairs. You should only edit properties that are described in the following section and leave others untouched.

## Configuration Properties in `gnizr-config.xml` ##

| **Property Name** | **Description** | **Default Value** | **Is Required** |
|:------------------|:----------------|:------------------|:----------------|
| `url`             | gnizr JDBC connection URL | `jdbc:mysql://localhost/gnizr_db` | Yes             |
| `username`        | gnizr JDBC connection username | `gnizr`           | Yes             |
| `password`        | gnizr JDBC connection password | `gnizr`           | Yes             |
| `webApplicationUrl` | The URL to your gnizr installation | `http://localhost:8080/gnizr`| No              |
| `googleMapsKey`   | [Google Maps API key](http://www.google.com/apis/maps/signup.html) of your site | _a key registered for `http://localhost`_| Yes             |
| `snapShotsKey`    | [SnapShots API key](http://www.snap.com/about/shots1.php) of your site |                   | No              |
| `anonymousReaderPolicy` | Whether you allow anonymous user to view contents published in gnizr. If `open`, anyone can view contents published in gnizr. If `close`, only logged in user is allowed to view published contents. | `open`            |  No             |
| `registrationPolicy` | Whether you required new user accounts to be created by system administrator. If `open`, anyone can register a new account via a self-register page. If `close`, new accounts must be manually created by gnizr administrator. | `open`            | No              |
| `serverMaintenanceModeEnabled` | Whether the application is run in a service maintenance mode. If `true`, only gnizr administrator is allowed to login. If `false`, any registered user can login. | `false`           | No              |

## Tips ##

Google Maps API key

| Domain URL | Key String |
|:-----------|:-----------|
| http://localhost:8080 | ABQIAAAA5UsQFqmHpHln5pC8nyp05RTwM0brOpm-All5BF6PoaKBxRWWERS\_Vpbx966CLXx9tDQthE6QI\_9\_bg |

## Related ##
  * HowToInstall
  * HowToBuild