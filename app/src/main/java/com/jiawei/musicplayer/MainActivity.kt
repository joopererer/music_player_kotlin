package com.jiawei.musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.jiawei.musicplayer.data.AppContainer
import com.jiawei.musicplayer.data.AppDataContainer
import com.jiawei.musicplayer.model.Datasource
import com.jiawei.musicplayer.model.MusicFile
import com.jiawei.musicplayer.ui.theme.MusicPlayerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    lateinit var data: Datasource
    val musicData = mutableStateListOf<MusicFile>()
    lateinit var container: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        container = AppDataContainer(this)
        data = Datasource(container.musicFilesRepository)
        super.onCreate(savedInstanceState)
        data.setObserver(this) {
            musicData.clear()
            musicData.addAll(data.loadMusicFiles() ?: listOf())
        }

        MusicPlayer.getMusicPlayer()?.init(this)

        setContent {
            MusicPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(Modifier.fillMaxSize(), musicData, data.showLoading.value)
                }
            }
        }
    }

    override fun hasFilePermission(hasPerssion: Boolean) {
        if(hasPerssion){
            data.scanFile()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier, musicList: List<MusicFile>, isLoading: Boolean) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = "${musicList.size} music files found")
                }
            )
        },
    ) {
        ListScreen(modifier.padding(it), musicList)
        LoadingDialog(isLoading, "Scanning file ...")
    }
}

@Composable
fun ListScreen(modifier: Modifier = Modifier, musicList: List<MusicFile>) {
    val player = MusicPlayer.getMusicPlayer()
    var isPlaying by remember { mutableStateOf(false) }
    var music_cur: MusicFile? by remember { mutableStateOf(null) }
    var progress by remember { mutableStateOf(0L) }
    var job: Job? by remember { mutableStateOf(null) }
    Column {
        MusicList(
            modifier
                .fillMaxWidth()
                .weight(1f),
            musicList,
            music_cur
        ) {
            playMusic(it)
            music_cur = it
            job = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    delay(1000) // Update progress every second
                    progress = player?.position() ?: 0L
                    isPlaying = player?.isPlaying()?:false
                }
            }
        }
        MusicControlBar(
            modifier = Modifier.fillMaxWidth(),
            isPlaying = isPlaying,
            music_cur = music_cur,
            position = progress,
            duration = player?.duration()?:0L,
            onValueChange = {
                progress = it
            },
            onValueChangeFinish = {
                player?.seek(progress)
            },
            onToggleClick = {
                isPlaying = !isPlaying
                if(!isPlaying) {
                    player?.pause()
                }else{
                    player?.resume()
                }
            }
        )
    }
}

@Composable
fun MusicControlBar(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    music_cur: MusicFile?,
    position: Long,
    duration: Long,
    onValueChange: (Long)->Unit,
    onValueChangeFinish: ()->Unit,
    onToggleClick: ()->Unit = {}
) {
    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
            .padding(10.dp, 25.dp, 10.dp, 10.dp)
        ) {
            // music info
            Column (
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 5.dp)
            ) {
                Text(
                    text = music_cur?.filename?:"",
                    fontSize = 16.sp
                )
                Text(
                    text = "<artist>",
                    fontSize = 13.sp
                )
            }

            // controller
            Row (
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // button prev
                Button(
                    modifier = Modifier
                        .width(50.dp)
                        .height(30.dp),
                    onClick = { /*TODO*/ }
                ) {
//                    Image(
//                        painter = painterResource(id = android.R.drawable.ic_media_previous),
//                        contentDescription = "prev_music"
//                    )
                    Text(text = "<", fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(5.dp))
                // button toggle
                Button(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp),
                    onClick = {
                        onToggleClick()
                    }
                ) {
//                    Image(
//                        painter = painterResource(id = android.R.drawable.ic_media_play),
//                        contentDescription = "toggle_music"
//                    )
                    var text = "P"
                    if(isPlaying) {
                        text = "S"
                    }
                    Text(text = text, fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(5.dp))
                // button next
                Button(
                    modifier = Modifier
                        .width(50.dp)
                        .height(30.dp),
                    onClick = { /*TODO*/ }
                ) {
//                    Image(
//                        painter = painterResource(id = android.R.drawable.ic_media_next),
//                        contentDescription = "next_music"
//                    )
                    Text(text = ">", fontSize = 10.sp)
                }
            }
        }
        // time
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp, 8.dp, 5.dp, 0.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left,
                text = milliSecondsToTimeString(position),
                fontSize = 15.sp
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                text = milliSecondsToTimeString(duration),
                fontSize = 15.sp
            )
        }
        // progress bar
        ProgressBar(
            position = position,
            duration = duration,
            onValueChange = onValueChange,
            onValueChangeFinish = onValueChangeFinish,
            modifier = modifier
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProgressBar(
    position: Long,
    duration: Long,
    onValueChange: (Long)->Unit,
    onValueChangeFinish: ()->Unit,
    modifier: Modifier = Modifier
) {
    Slider(
        value = position.toFloat(),
        onValueChange = {
            onValueChange(it.toLong())
        },
        onValueChangeFinished = {
            onValueChangeFinish()
        },
        valueRange = 0f..duration.toFloat(),
        modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
    )
}

@Composable
fun MusicList(modifier: Modifier = Modifier, musicList: List<MusicFile>, currentMusic: MusicFile? = null, onClick: (MusicFile) -> Unit) {
    LazyColumn(
        modifier = modifier
    ){
        items(musicList) {
            music -> MusicItem(music=music, isPlaying = (music==currentMusic), onClick = onClick)
        }
    }
}

@Composable
fun MusicItem(modifier: Modifier = Modifier, music: MusicFile, isPlaying: Boolean, onClick: (MusicFile)->Unit = {}) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick(music)
                }
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(35.dp, 10.dp, 10.dp, 10.dp)
            ) {
                Column {
                    // filename
                    Text(text = music.filename)
                    // dir
                    Text(
                        text = music.dir,
                        fontSize = 10.sp
                    )
                }
                // music type
                Text(
                    text = music.type,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 2.dp, end = 5.dp)
                )
            }
            if(isPlaying) {
                RandomJumpingSpectrum(Modifier.padding(7.dp).size(20.dp))
            }
        }
        Divider()
    }
}

@Composable
fun LoadingDialog(isLoading: Boolean, message: String = "Loading...") {
    if (isLoading) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(100.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = message)
                }
            }
        }
    }
}

fun playMusic(music: MusicFile) {
    MusicPlayer.getMusicPlayer()?.play(music.path)
}

fun milliSecondsToTimeString(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

@Preview(showBackground = true)
@Composable
fun ListItemPreview() {
    MusicPlayerTheme {
//        MainScreen(Modifier.fillMaxSize(), musicList = emptyList(), isLoading = false)
//        MusicItem(MusicFile("file://folder/my_music/music_01.mp3"))
//        MusicList(Datasource().loadMusicFiles())
        val music = MusicFile("file://folder/my_music/music_01.mp3")
        MusicControlBar(Modifier.fillMaxWidth(),false, music,0,1200,{}, {})
//        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun MusicItemPreview() {
    MusicItem(music = MusicFile("file://folder/my_music/music_01.mp3"), isPlaying = true)
}
