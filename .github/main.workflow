workflow "check build on push" {
  on = "push"
  resolves = ["gradle build"]
}

action "gradle build" {
  uses = "MrRamych/gradle-actions@12909e7ccd3ed7e3b39c2f3ac350d6849eabeaf3"
  args = "build"
}

workflow "process pull request" {
  on = "pull_request"
  resolves = ["post gif on fail", "branch cleanup"]
}

action "post gif on fail" {
  uses = "jessfraz/shaking-finger-action@master"
  secrets = ["GITHUB_TOKEN"]
}

action "branch cleanup" {
  uses = "jessfraz/branch-cleanup-action@master"
  secrets = ["GITHUB_TOKEN"]
}
