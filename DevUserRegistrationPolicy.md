| **NOTE**: This document is for gnizr 2.4 only |
|:----------------------------------------------|

Gnizr implements a policy-driven user registration function. Depending on the configuration, system administrators can choose to implement one of the following user registration policy:

  1. _Open Policy_: allow any one with a valid email address to create and activate new user accounts.
  1. _Approval Policy_: allow any one to create new accounts, but their activation requires the explicit approval from the administrator.
  1. _Close Policy_: prohibit any one to create new account, and only the administrator can create and activate new user accounts.

## Configuration ##

To set the user registration policy, system administrators should edit the `registrationPolicy` property in `gnizr-config.xml`.

Valid values for `registrationPolicy`:
  * `open` -- for Open Policy
  * `close` -- for Close Policy
  * `approval` -- for Approval Policy

If this property is not defined, the default value is `open`.


| **NOTE**: Setting the property value to either `open` or `approval` will require the proper configuration of SMTP server (i.e., the `mailSender` property) and provide a valid sender email (i.e., the `siteContactEmail`) in `gnizr-config.xml`. |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

## Design ##

The design for Close Policy implementation is as follows. If the user registration policy is `close`, then any one who attempts to invoke the user registration pages will receive a notice message, which says the user registration function is closed.

For `open` and `approval` policy implementation, the business logic is depicted in the following UML diagrams:

| [![](http://farm3.static.flickr.com/2253/2453931161_cdda24535f_m.jpg)](http://www.flickr.com/photos/14804582@N08/2453931161/) | [![](http://farm3.static.flickr.com/2394/2454757210_53a85f1a04_m.jpg)](http://www.flickr.com/photos/14804582@N08/2454757210/)|
|:------------------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------|