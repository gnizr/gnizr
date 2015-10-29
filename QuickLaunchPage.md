This page describes basic features to help you get started using gnizr. For advanced features and a detailed user guide, read the UserGuide page.

## Before Using Gnizr ##

  1. Make sure gnizr is probably installed and is accessible
  1. Create an user account for yourself, or get an account from your administrator

In this document, we assume your gnizr application is accessible at http://gnizr.yoursite.com, and your user account is `harryn`.

### For Administrators ###

All gnizr installations come with a default "super" user: `gnizr`. This account has the administrative capability to change gnizr runtime configurations. The **default password** of this account is `gnizr`. For security reasons, **YOU SHOULD CHANGE THIS PASSWORD**.

### For Users ###

After you have logged in to your account, you can change your password under `settings`, `Change your password`.

## How do I login and logout? ##

Log into your account at gnizr's front page (e.g., http://gnizr.yoursite.com).

| ![http://farm3.static.flickr.com/2242/1526175068_8103454071.jpg](http://farm3.static.flickr.com/2242/1526175068_8103454071.jpg) |
|:--------------------------------------------------------------------------------------------------------------------------------|


The `logout` link is the located on the top-right of your user home page.

| ![http://farm3.static.flickr.com/2232/1525658597_a7b40b6bfc.jpg](http://farm3.static.flickr.com/2232/1525658597_a7b40b6bfc.jpg) |
|:--------------------------------------------------------------------------------------------------------------------------------|


---

## What am I seeing on my Homepage? ##

### Navigation Bar ###
The Navigation bar allows the user to navigate through their personal bookmarks, analyze bookmarks as well as log out and see other peoples bookmarks.

  * Current Page Location: Breadcrumb trail to show the user's current viewing location.
  * Personal Analysis Tools: These links allow the user to view bookmarks in different ways, enabling analysis on them.  In addition the user is able to post as well as view bookmarks supplies to them by other users.
  * User Status and Help: This shows the acount that is logged into gnizr, and allows the user an option to log out.  The help link is located here as well.
  * Other Tools: The links that allows the user to work on folders, archived bookmarks as well as modify and edit RSS subscriptions.
  * User Utilities: These are quick links to different sections of gnizr.  The user is able to add a bookmark, view bookmarks that have been assigned to you by other people or robots, or search for bookmarks.

|![http://farm3.static.flickr.com/2034/1525658645_7df0f912d4.jpg](http://farm3.static.flickr.com/2034/1525658645_7df0f912d4.jpg)|
|:------------------------------------------------------------------------------------------------------------------------------|

### Bookmark ###
Bookmarks display information about the page or item that is bookmarked.  The information that is listed for users to see is as follows:

  * Bookmark Link: This gives the page title as well as serving as a link to the page itself.
  * Bookmark Creator: This lets users know who created the bookmark.  It also allows users a link to that user's page.
  * Number of users for a Bookmark: This informs users how many users have this particular bookmark on their bookmark page.
  * Commands: This allows the user to edit the bookmark.
  * Tags:  This is a listing of tags for that particular bookmark.  They also serve as links that allow the user to search on that particular tag.
  * Creation Date:  This informs users as to what day the bookmark was created.
  * Pop-up Preview: When the user mouses over the icon it displays a small view of the page to which it is linked.

|![http://farm3.static.flickr.com/2152/1526525816_64082897ba.jpg](http://farm3.static.flickr.com/2152/1526525816_64082897ba.jpg)|
|:------------------------------------------------------------------------------------------------------------------------------|

### Tag Cloud ###
The tag cloud contains many different ways to view as well as edit new and existing tags.

  * Tag Relations: Allows the user to create relationships between tags to enable easy navigation and comparisons.
  * Tag Listing: Lists the tags for the bookmarks that are being viewed.
  * Tag Cloud Options: This allows the user to easily modify the tag cloud view and properties

|![http://farm3.static.flickr.com/2301/1526525874_bd07bf59c2.jpg](http://farm3.static.flickr.com/2301/1526525874_bd07bf59c2.jpg)|
|:------------------------------------------------------------------------------------------------------------------------------|

## How do I create a bookmark? ##
To save a new bookmark to your gnizr account:

  1. On the toolbar across the top of the page there is a "+bookmark" link which will allow you to post new bookmarks to your account.  Or, your can go directly to http://yoursite/post.
  1. Fill out the "save bookmark" form and click "save".

Form field descriptions:

  * url: the URL of the bookmark that you want to save
  * description: a short title description of the bookmark
  * notes: an optional detail description or comments about the bookmark
  * tags: a list of keywords that describe this bookmark. Separate multiple tags spaces.

|![http://farm3.static.flickr.com/2035/1525733003_46e4b03d23.jpg](http://farm3.static.flickr.com/2035/1525733003_46e4b03d23.jpg)|
|:------------------------------------------------------------------------------------------------------------------------------|
|![http://farm3.static.flickr.com/2245/1526600708_c02aaf3999.jpg](http://farm3.static.flickr.com/2245/1526600708_c02aaf3999.jpg)|


---

## How do I find bookmarks? ##

### How do I Find bookmarks using tags? ###

To quickly find bookmarks that you have previously saved, you can exploit your tag cloud. For example, you have saved a bookmark
of http://www.cnn.com and tagged it using "news" and "cnn". To find this bookmark, in your tag cloud, click on either "cnn" or "news".

You can also go directly to bookmarks of a particular tag via URL.

  * Your bookmarks tagged "cnn": http://yoursite/user/yourusername/tag/cnn
  * Your bookmarks tagged "news": http://yoursite/user/yourusername/tag/news

You can also see bookmarks tagged by other users via URL

  * Bookmarks tagged "cnn" : http://yoursite/tag/cnn

### How do I find bookmarks using the search box? ###

You can search for bookmarks that you certain criteria that you have defined.  There are two ways to search bookmarks:

  1. Match text: search texts in either the description or the notes field of a bookmark
> 2. Match tags: search texts in the tags field of a bookmark

The search syntax of Match text supports natural language text search. For example

  * java programming related to database
  * web technology on knowledge management

The search syntax of Match tags supports various logical operations. For example,

  * "apple banana": find bookmarks that have at least one of the two tags.
  * "+apple +juice": find bookmarks that have both tags.
  * "+apple macintosh": find bookmarks that have the tag "apple", but rank the search score of those bookmarks higher if they also have the tag "macintosh".
  * "+apple -macintosh": find bookmarks that have the tag "apple" but not "macintosh".
  * "+apple ~macintosh": find bookmarks that have the tag "apple", but if the bookmark also have the tag "macintosh", rate it lower than if the bookmark does not.
  * "apple"**: find bookmarks that have tags such as "apple", "apples", "applesauce", or "applet".**


---

## How do I find other user's bookmarks? ##

### How do I Find other user's bookmarks using tags? ###

The easiest way to search fro a bookmark using tags is to see bookmarks tagged by other users via URL as the following example describes.

  * Bookmarks tagged "cnn" : http://yoursite/tag/cnn

### How do I find bookmarks using the search box? ###

To search for community bookmarks with the search box, the user need only to enter the desired search into the search text box and choose community from the dropdown box and hit the search button.

  * java programming related to database
  * web technology on knowledge management

The search syntax of Match tags supports various logical operations. For example,

  * "apple banana": find bookmarks that have at least one of the two tags.
  * "+apple +juice": find bookmarks that have both tags.
  * "+apple macintosh": find bookmarks that have the tag "apple", but rank the search score of those bookmarks higher if they also have the tag "macintosh".
  * "+apple -macintosh": find bookmarks that have the tag "apple" but not "macintosh".
  * "+apple ~macintosh": find bookmarks that have the tag "apple", but if the bookmark also have the tag "macintosh", rate it lower than if the bookmark does not.
  * "apple"**: find bookmarks that have tags such as "apple", "apples", "applesauce", or "applet".**


---

## RSS Subscription machine tag ##

Syntax: subscribe:this
Usage: request gnizr to monitor an RSS feed and automatically import new feed items
Notes: This machine tag must be used on a bookmark whose URL is a valid RSS feed. New feeds are monitored by the gnizr system
periodically. When a new feed items are found, they will appear on the "for you" page.

Another option is to select the RSS subscription link contained within the "Other Tools" section of the toolbar.  At the bottom of this page there is a text box where the user is able to enter the URL for a feed and select the subscribe button.  This will then take the user to the following page where they are able to set the frequency and location the bookmarks are saved.

|![http://farm3.static.flickr.com/2328/1525780467_19145b770a.jpg](http://farm3.static.flickr.com/2328/1525780467_19145b770a.jpg)|
|:------------------------------------------------------------------------------------------------------------------------------|

#### Location of the for you link ####
|![http://farm3.static.flickr.com/2225/1525658879_9b5fb5fffe.jpg](http://farm3.static.flickr.com/2225/1525658879_9b5fb5fffe.jpg)|
|:------------------------------------------------------------------------------------------------------------------------------|