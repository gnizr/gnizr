| **NOTE**: This document is for gnizr 2.4 only.  |
|:------------------------------------------------|

This page describes the specification of gnizr XML configuration file. This configuration controls the JDBC connectivity to gnizr MySQL database and the runtime behavior of a gnizr deployment.

## Edit gnizr-config.xml ##

The gnizr web application requires a configuration file called `gnizr-config.xml`. You can create this file by following a sample configuration located in the directory `gnizr/WEB-INF/classes`. The filename of this sample configuration is called `gnizr-config-sample.xml`.

You should edit this configuration file to suit your machine's configuration. Configurations are defined as a set of property/value pairs. You should only edit properties that are described in the following section and leave others untouched.

## Configuration Properties in `gnizr-config.xml` ##

| **Property Name** | **Description** | **Default Value** | **Is Required** |
|:------------------|:----------------|:------------------|:----------------|
| `url`             | gnizr JDBC connection URL | `jdbc:mysql://localhost/gnizr_db` | Yes             |
| `username`        | gnizr JDBC connection username | `gnizr`           | Yes             |
| `password`        | gnizr JDBC connection password | `gnizr`           | Yes             |
| `webApplicationUrl` | The URL to your gnizr installation | `http://localhost:8080/gnizr`| No              |
| `siteName`        | A custom name for this gnizr installation | `gnizr`           | Yes             |
| `siteDescription` | A short description of this gnizr installation | `organize`        | No              |
| `siteContactEmail` | The email of the site admin. If `registrationPolicy` is `closed`, the Registration page will direct new users to send new account request to this email address | _empty string_    | Yes             |
| `googleMapsKey`   | [Google Maps API key](http://www.google.com/apis/maps/signup.html) of your site | _a key registered for `http://localhost`_| Yes             |
| `snapShotsKey`    | [SnapShots API key](http://www.snap.com/about/shots1.php) of your site |                   | No              |
| `anonymousReaderPolicy` | Whether you allow anonymous user to view contents published in gnizr. If `open`, anyone can view contents published in gnizr. If `close`, only logged in user is allowed to view published contents. | `open`            |  No             |
| `registrationPolicy` | Whether you required new user accounts to be created by system administrator. Valid options: `open`,`close` and `approval`. See DevUserRegistrationPolicy| `open`            | No              |
| `serverMaintenanceModeEnabled` | Whether the application is run in a service maintenance mode. If `true`, only gnizr administrator is allowed to login. If `false`, any registered user can login. | `false`           | No              |
| `openSearchServices` | A list of OpenSearch service description document URL | See [OpenSearch documentation](DevOpenSearch.md) | Yes             |
| `searchIndexDirectory` | An existing directory path for storing Lucene search index | `C:\search-data`  | Yes             |
| `resetSearchIndexOnStart` | A flag tells gnizr whether to reset the search index when it starts. On reset, all content in the directory defined by `searchIndexDirectory` will be deleted. (obsolete since 2.4.0-M2) |  `false`          | No              |
| `searchSuggestDataFile` | A dictionary file used for building search suggestion. Site admins can add new terms to this file. See also DevSearchTermSuggestion | `/dictionary/default.txt` | No              |
| `suggestPopularTagsEnabled` | Configures whether or not frequently used tags should be used to build search suggest dictionary index. | `true`            | No              |
| `mailSender`      | Defines the configuration for communicating with an SMTP server. See DevMailSenderConfig (available since 2.4.0-M3)| `localhost`       | Yes             |

## Tips ##

Google Maps API key

| Domain URL | Key String |
|:-----------|:-----------|
| http://localhost:8080 | ABQIAAAA5UsQFqmHpHln5pC8nyp05RTwM0brOpm-All5BF6PoaKBxRWWERS\_Vpbx966CLXx9tDQthE6QI\_9\_bg |

## Related ##
  * HowToInstall
  * HowToBuild