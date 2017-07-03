
Description
-------------------------

All original code come from http://sourceforge.net/projects/redmin-mylyncon/

Please note that this fork is effectively abandoned. I forked from pahjbo solely to see whether I could get travis-ci to build this so I could install with his fix for displaying issue IDs in Neon+. The way I got there is kind of a mess.

Build status on Travis CI:
[![Build Status](https://travis-ci.org/scriptninja/redmine-mylyn-plugin.svg)](https://travis-ci.org/scriptninja/redmine-mylyn-plugin)

Redmine-Mylyn integration
-------------------------

Redmine-Mylyn integration requires 2 components to work:

1. Redmine plugin (connector) - is installed inside Redmine server and publishes an API
2. Eclipse plugin - acts like a client for API from (1) plugin

The Mylyn project in particular has always called the component that adds support for another server a "connector", 
and Redmine usually calls additional components "plugin" - alas, 
the original software project got this the wrong way around and called the server-side component a connector
and the client side component a plugin. Confusion persists unto this day.

Connector Installation
-------------------------

1. Install connectior inside Redmine server
    
    For Redmine 2.x install Connector from:
    [http://danmunn.github.io/redmine_mylyn_connector/](http://danmunn.github.io/redmine_mylyn_connector/)
     
    For Redmine 3.x install Connector from:
    [TODO Link](http://google.com)
     
2. Enable REST API in Redmine server

    This is required, because Eclipse plugin uses REST API to authenticate user and perform requests. 

    To enable REST API go to yours redmine Administration -> Settings -> Authentication tab and toggle on:
    **Enable REST web service** and press **Save**

Plugin Installation
-------------------------

1.  Install from update site:
[P2 Update Site](https://scriptninja.github.io/redmine-mylyn-plugin/net.sf.redmine_mylyn.p2repository/target/repository/)

2. Add Task Repository of type Redmine and provide redmine Url with Login and Password

Development
-------------------------

See the [CONTRIBUTING.md](CONTRIBUTING.md).

Licence
-------------------------
Licence remains as in original plugin, that is:
Eclipse Public License, GNU General Public License (GPL)
