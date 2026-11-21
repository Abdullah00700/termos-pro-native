package com.termos.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TermOSProTheme {
                TerminalScreen()
            }
        }
    }
}

@Composable
fun TermOSProTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF38BDF8),
            background = Color(0xFF090D16),
            surface = Color(0xFF0F172A),
            onBackground = Color(0xFFF8FAFC),
            onSurface = Color(0xFFF8FAFC)
        ),
        content = content
    )
}

data class TerminalLine(
    val text: String,
    val color: Color = Color(0xFFCBD5E1)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen() {
    var currentDir by remember { mutableStateOf("/home/user") }
    var inputCommand by remember { mutableStateOf("") }
    val terminalLines = remember {
        mutableStateListOf(
            TerminalLine("Welcome to TermOS Pro v3.7 (armv8l) - Pure Android Native", Color(0xFF38BDF8)),
            TerminalLine("• System: Motorola Edge 60 Fusion • Android 17 (SDK 37) • 11.4 GB RAM", Color(0xFF64748B)),
            TerminalLine("• Type 'help' for available Linux commands, or 'pkg install python'.\n", Color(0xFF64748B))
        )
    }
    
    val virtualFiles = remember {
        mutableStateMapOf(
            "/home/user/hello.py" to "print('Hello from Python 3 on Native Android!')",
            "/home/user/Main.kt" to "fun main() {\n    println('Hello from Kotlin on Android!')\n}",
            "/etc/motd" to "Welcome to TermOS Pro Linux Environment!"
        )
    }

    val coroutineScope = rememberCoroutineScope()

    fun execute(cmd: String) {
        val trimmed = cmd.trim()
        if (trimmed.isEmpty()) return

        terminalLines.add(TerminalLine("u0_a312@android:$currentDir$ $trimmed", Color(0xFF22C55E)))
        
        val parts = trimmed.split(" ")
        val command = parts[0]
        val args = parts.drop(1)

        when (command) {
            "help" -> {
                terminalLines.add(TerminalLine("Available Native Commands:", Color(0xFF38BDF8)))
                terminalLines.add(TerminalLine("  ls [-la]             List files in directory"))
                terminalLines.add(TerminalLine("  cd <dir>             Change directory"))
                terminalLines.add(TerminalLine("  pwd                  Print working directory"))
                terminalLines.add(TerminalLine("  cat <file>           Display file contents"))
                terminalLines.add(TerminalLine("  mkdir <dir>          Create directory"))
                terminalLines.add(TerminalLine("  touch <file>         Create empty file"))
                terminalLines.add(TerminalLine("  python <file>        Run Python script"))
                terminalLines.add(TerminalLine("  kotlin <file>        Run Kotlin script"))
                terminalLines.add(TerminalLine("  neofetch             Show system specifications"))
                terminalLines.add(TerminalLine("  pkg install <pkg>    Install package"))
                terminalLines.add(TerminalLine("  clear                Clear terminal screen"))
                terminalLines.add(TerminalLine("  apt update           Update package repository"))
                terminalLines.add(TerminalLine("  git clone <url>      Clone git repository"))
            }
            "clear" -> terminalLines.clear()
            "pwd" -> terminalLines.add(TerminalLine(currentDir))
            "whoami" -> terminalLines.add(TerminalLine("u0_a312"))
            "uname" -> terminalLines.add(TerminalLine("Linux android 17.0.0-scout #1 SMP PREEMPT_RT armv8l Android"))
            "ls" -> {
                val filesInDir = virtualFiles.keys.filter { it.startsWith(currentDir) }
                if (filesInDir.isEmpty()) {
                    terminalLines.add(TerminalLine("(empty directory)", Color(0xFF64748B)))
                } else {
                    filesInDir.forEach { f ->
                        terminalLines.add(TerminalLine(f.substringAfterLast("/"), Color(0xFF38BDF8)))
                    }
                }
            }
            "cd" -> {
                if (args.isEmpty() || args[0] == "~") {
                    currentDir = "/home/user"
                } else {
                    currentDir = "/home/user/${args[0].removePrefix("/")}"
                }
            }
            "cat" -> {
                val target = if (args.isNotEmpty()) args[0] else ""
                val fullPath = if (target.startsWith("/")) target else "$currentDir/$target"
                val content = virtualFiles[fullPath]
                if (content != null) {
                    terminalLines.add(TerminalLine(content, Color(0xFFE2E8F0)))
                } else {
                    terminalLines.add(TerminalLine("cat: $target: No such file or directory", Color(0xFFEF4444)))
                }
            }
            "touch" -> {
                val target = if (args.isNotEmpty()) args[0] else "new_file.txt"
                val fullPath = "$currentDir/$target"
                virtualFiles[fullPath] = ""
                terminalLines.add(TerminalLine("Created $fullPath", Color(0xFF22C55E)))
            }
            "mkdir" -> {
                val target = if (args.isNotEmpty()) args[0] else "new_dir"
                terminalLines.add(TerminalLine("Created directory $currentDir/$target", Color(0xFF22C55E)))
            }
            "neofetch" -> {
                terminalLines.add(TerminalLine("       /\\        u0_a312@android", Color(0xFF38BDF8)))
                terminalLines.add(TerminalLine("      /  \\       ----------------------------", Color(0xFF38BDF8)))
                terminalLines.add(TerminalLine("     / /\\ \\      OS: TermOS Pro Linux (Android 17 armv8l)"))
                terminalLines.add(TerminalLine("    / /__\\ \\     Host: Motorola Edge 60 Fusion (scout)"))
                terminalLines.add(TerminalLine("   / /----\\ \\    Kernel: 17.0.0-scout-rt"))
                terminalLines.add(TerminalLine("  /_/      \\_\\   Memory: 1842MB / 11420MB (16% RAM)"))
            }
            "pkg", "apt" -> {
                val action = args.getOrNull(0)
                if (action == "install" || action == "update" || action == "upgrade") {
                    val pkgName = args.getOrNull(1) ?: "packages"
                    terminalLines.add(TerminalLine("Hit:1 https://packages.termos.org/android stable InRelease", Color(0xFF64748B)))
                    terminalLines.add(TerminalLine("Reading package lists... Done", Color(0xFF64748B)))
                    terminalLines.add(TerminalLine("Building dependency tree... Done", Color(0xFF64748B)))
                    terminalLines.add(TerminalLine("Setting up $pkgName (3.11.4-armv8l) ... Done.", Color(0xFF22C55E)))
                } else {
                    terminalLines.add(TerminalLine("Usage: pkg install <package> or apt update", Color(0xFFEAB308)))
                }
            }
            "python" -> {
                val target = args.getOrNull(0) ?: "hello.py"
                val fullPath = if (target.startsWith("/")) target else "$currentDir/$target"
                val code = virtualFiles[fullPath] ?: "print('Running $target')"
                terminalLines.add(TerminalLine("[Python 3.11 Execution]", Color(0xFFEAB308)))
                terminalLines.add(TerminalLine(code, Color(0xFFE2E8F0)))
                terminalLines.add(TerminalLine("Process exited with code 0", Color(0xFF64748B)))
            }
            "kotlin" -> {
                val target = args.getOrNull(0) ?: "Main.kt"
                val fullPath = if (target.startsWith("/")) target else "$currentDir/$target"
                val code = virtualFiles[fullPath] ?: "println('Running $target')"
                terminalLines.add(TerminalLine("[Kotlin 1.9 Native Compiler & Runtime]", Color(0xFFEAB308)))
                terminalLines.add(TerminalLine("kotlinc $target -include-runtime -d app.jar && java -jar app.jar", Color(0xFF64748B)))
                terminalLines.add(TerminalLine(code, Color(0xFFE2E8F0)))
                terminalLines.add(TerminalLine("Process exited with code 0", Color(0xFF64748B)))
            }
            "git" -> {
                if (args.getOrNull(0) == "clone") {
                    val url = args.getOrNull(1) ?: "repository"
                    terminalLines.add(TerminalLine("Cloning into repository from $url...", Color(0xFF64748B)))
                    terminalLines.add(TerminalLine("Receiving objects: 100% (42/42), done.", Color(0xFF22C55E)))
                } else {
                    terminalLines.add(TerminalLine("git: 'git ${args.joinToString(" ")}' is not a git command. See 'git --help'.", Color(0xFFEF4444)))
                }
            }
            else -> {
                terminalLines.add(TerminalLine("bash: $command: command not found. Type 'help' for available commands.", Color(0xFFEF4444)))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                        Text("TermOS Pro", fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                        Surface(
                            color = Color(0xFF0369A1).copy(alpha = 0.4f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("Android 17", fontSize = 10.sp, color = Color(0xFF38BDF8), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
            ) {
                // Mobile Shortcut Toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val shortcuts = listOf("ls -la", "pwd", "neofetch", "help", "pkg install python", "clear", "Tab", "Ctrl", "Esc", "|", "~", "/")
                    shortcuts.forEach { sc ->
                        Button(
                            onClick = {
                                if (sc == "clear" || sc == "help" || sc == "pwd" || sc == "neofetch" || sc == "ls -la") {
                                    execute(sc)
                                } else if (sc == "Tab" || sc == "Ctrl" || sc == "Esc") {
                                    terminalLines.add(TerminalLine("Key [$sc] pressed", Color(0xFF64748B)))
                                } else {
                                    inputCommand = sc
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(sc, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF38BDF8))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "u0_a312:~$",
                        color = Color(0xFF22C55E),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = inputCommand,
                        onValueChange = { inputCommand = it },
                        modifier = Modifier.weight(1.dp),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = Color.White),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            execute(inputCommand)
                            inputCommand = ""
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF1E293B)
                        )
                    )
                    Button(
                        onClick = {
                            execute(inputCommand)
                            inputCommand = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF090D16))
                .padding(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(terminalLines) { line ->
                    Text(
                        text = line.text,
                        color = line.color,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
