# Jotai-CLJ

Clojure/Script example app using [UIx](https://github.com/pitch-io/uix) and [Jotai](https://jotai.org/).

![Screenshot of the example app user interface](screenshot.png?raw=true)

[UIx](https://github.com/pitch-io/uix) provides low-overhead interface to React, and it works with the latest version of React.
- Working with React JS libraries is fairly painless. For example, hooks and providers work out of the box with the default UI macro.
- Syntactically using `($ :element opts body)` macro is not much different from hiccup, but it needs some time "getting used to". For example, following is sort of fairly common pattern in Reagent, but not so much in UIx:
```clojure
(into [:<>]
      (for [id todos]
        [todo-component {:id id}]))
```
- On the other hand, UIx version is actually kind of nice too:
```clojure
($ :<>
   (for [id todos]
     ($ todo-component {:id id :key id})))
```

[Jotai](https://jotai.org/) implements state management using it's flavor of atoms. It composes fairly nicely and works with the latest version of React.
- I wanted to try a "more native" solution, since UIx (and shadow-cljs) make it easy to interop with JS libraries.
- I have previously used [Recoil](https://github.com/facebookexperimental/Recoil) which is very much similar to Jotai, but Recoil has not seen new releases since 03/2023, and quite a few questions about project abandonment have gone unanswered.
- `re-frame` is based on Reagent, and to my taste it has a bit too much ceremony. I have used re-frame almost exclusively for past 3+ years.
- Jotai was easy to get started with. The wrapper functions in this repo just make it easier to compose e.g. setter functions.
- Jotai has some familiar API from Recoil. For example, atom family is a technique for "subscribing" to atoms dynamically (useful for e.g. infinite scrolling with editable components).
- Ability to query and manipulate atoms via store object works quite nice in REPL workflows.

## Running the Example

(forked from [shadow-cljs - browser quickstart](https://github.com/shadow-cljs/quickstart-browser))

```bash
npm run server
```

This runs the `shadow-cljs` server process which all following commands will talk to. Just leave it running and open a new terminal to continue.

The first startup takes a bit of time since it has to download all the dependencies and do some prep work. Once this is running we can get started.

```bash
npm run dev
```

This will begin the compilation of the configured `:app` build and re-compile whenever you change a file.

When you see a "Build completed." message your build is ready to be used.

```txt
[:app] Build completed. (23 files, 4 compiled, 0 warnings, 7.41s)
```

You can now then open [http://localhost:8020](http://localhost:8020).
