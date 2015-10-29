Key steps to cut a release from the gnizr code base. This document is intended for the gnizr project members only.

  1. Check out the branch of the source from SVN
  1. Build the software by following HowToBuild
  1. Test the bulid in a fresh environment
    * Reload gnizr database schema and stored procedures in MySQL
    * Use the default `gnizr-config.xml` whenever possible
    * Get the binary from the `target` directory. Deploy `gnizr` in a fresh Tomcat server
  1. Verify [key issues](http://code.google.com/p/gnizr/issues/list) reported are fixed
  1. Verify software version is correct in (1) the login page and (2) version tag in the HTML page source
  1. Tag the branch in SVN
  1. Rename the binary zip to `gnizr-x.y.z.zip` and upload it onto [Downloads](http://code.google.com/p/gnizr/downloads/list)