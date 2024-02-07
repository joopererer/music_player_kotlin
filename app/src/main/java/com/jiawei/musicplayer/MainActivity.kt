package com.jiawei.musicplayer

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import com.jiawei.musicplayer.model.Datasource
import com.jiawei.musicplayer.model.MusicFile
import com.jiawei.musicplayer.ui.theme.MusicPlayerTheme

class MainActivity : BaseActivity() {

    lateinit var data: Datasource
    val musicData = mutableStateListOf<MusicFile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        data = Datasource()
        super.onCreate(savedInstanceState)
        data.setObserver(this, Observer {
            musicData.clear();
            musicData.addAll(data.loadMusicFiles())
        })

        setContent {
            MusicPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ListScreen(musicData)
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

@Composable
fun ListScreen(musicList: List<MusicFile>, modifier: Modifier = Modifier) {
    Column {
        MusicList(musicList, Modifier.fillMaxWidth().weight(1f))
        MusicControlBar(Modifier.fillMaxWidth())
    }
}

@Composable
fun MusicControlBar(modifier: Modifier = Modifier) {
    var isPlay by remember { mutableStateOf(false) }
    var music_current = MusicFile("", "music_01", "file://folder/my_music/", "mp3")
    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
            .padding(10.dp, 15.dp, 10.dp, 10.dp)
        ) {
            // music info
            Column (modifier = Modifier.weight(1f)) {
                Text(
                    text = music_current.filename,
                    fontSize = 15.sp
                )
                Text(
                    text = "<artist>",
                    fontSize = 12.sp
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
                    onClick = { isPlay = !isPlay }
                ) {
//                    Image(
//                        painter = painterResource(id = android.R.drawable.ic_media_play),
//                        contentDescription = "toggle_music"
//                    )
                    var text = "P"
                    if(isPlay) {
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
        // progress bar
        ProgressBar()
    }
}

@Composable
fun ProgressBar() {
    LinearProgressIndicator(
        progress = 0.5f,
        color = Color.Yellow,
        modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(shape = RoundedCornerShape(5.dp))
    )
}

@Composable
fun MusicList(musicList: List<MusicFile>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
    ){
        items(musicList) {
            music -> MusicItem(music)
        }
    }
}

@Composable
fun MusicItem(music: MusicFile, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp, 5.dp, 10.dp, 5.dp)
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
        Divider()
    }
}

@Preview(showBackground = true)
@Composable
fun ListItemPreview() {
    MusicPlayerTheme {
//        MusicItem(MusicFile("music_01", "file://folder/my_music/", "mp3"))
//        MusicList(Datasource().loadMusicFiles())
        MusicControlBar()
    }
}