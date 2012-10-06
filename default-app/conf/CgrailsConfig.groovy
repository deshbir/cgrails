cgrails {
	skinning {
		baseskin = "default"
		defaultskin = "default"
		skins {
			skin1 { parent = "default" }
		}		
	}
	workflows {
		defaultwokflow = "singlepage"
	}
	less {
		//Array of Less Files to compile.
		files = ["styles"]
	}
	javascriptMVC = "backbone"
}