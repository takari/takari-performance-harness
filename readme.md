This code was extracted from Eclipse Platform releng git repository [1] using
git-subtree command [2]. The code from Eclipse is kept on 'eclipse' branch and
the following steps are expected to pick latest changes from Eclipse.

Clone Eclipse Platform releng repository

    $ <go to a temp directory>
    $ git clone git://git.eclipse.org/gitroot/platform/eclipse.platform.releng.git
    $ cd eclipse.platform.releng

Split org.eclipse.test.performance code to 'perf-harness' branch. This takes about
one minute on 15-inch Late 2011 MacBook Pro.

    $ git subtree split --prefix=bundles/org.eclipse.test.performance \
          --annotate="(split)" --branch=perf-harness 

Push new commits to 'eclipse' branch of this repository.

    $ git push <path-to-this-repository> perf-harness:eclipse

Merge 'eclipse' branch to 'master' branch, resolve merge conflicts as necessary

    $ <go to this repository>
    $ git merge eclipse


[1] http://git.eclipse.org/c/platform/eclipse.platform.releng.git/
[2] https://github.com/git/git/blob/master/contrib/subtree/git-subtree.txt
