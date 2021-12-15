everything in this folder and subfolders is is a copy of `/libs/granite/core/content/login`

Files with differences compared to the source files are

* `/apps/granite/core/content/login/clientlib/resources/bg/default/bg.css`:
  * for every background-image url the path was prefixed with `../`
* `/apps/granite/core/content/login/errors/.content.xml`:
  * `invalid_authorization` section was added form line 16 to line 18
